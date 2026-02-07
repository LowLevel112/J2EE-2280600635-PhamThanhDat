let currentUserRoles = [];

$(document).ready(function () {
    // Lấy thông tin user hiện tại
    $.ajax({
        url: 'http://localhost:8080/api/v1/me',
        type: 'GET',
        dataType: 'json',
        success: function (userData) {
            currentUserRoles = userData.roles || [];
            
            // Kiểm tra xem có search keyword từ URL không
            const urlParams = new URLSearchParams(window.location.search);
            const keyword = urlParams.get('keyword');
            
            if (keyword) {
                loadSearchBooks(keyword);
            } else {
                loadBooks();
            }
        },
        error: function () {
            currentUserRoles = [];
            
            const urlParams = new URLSearchParams(window.location.search);
            const keyword = urlParams.get('keyword');
            
            if (keyword) {
                loadSearchBooks(keyword);
            } else {
                loadBooks();
            }
        }
    });

    // Handle search form submission
    $('form[action="/books/search"]').on('submit', function(e) {
        e.preventDefault();
        const keyword = $(this).find('input[name="keyword"]').val().trim();
        if (keyword) {
            loadSearchBooks(keyword);
            window.history.pushState({}, '', '/books?keyword=' + encodeURIComponent(keyword));
        }
    });
});

function loadSearchBooks(keyword) {
    $.ajax({
        url: 'http://localhost:8080/api/v1/books/search?keyword=' + encodeURIComponent(keyword),
        type: 'GET',
        dataType: 'json',
        success: function (data) {
            renderBooks(data, 'Search results for: ' + keyword);
        },
        error: function () {
            alert('Error searching books!');
        }
    });
}

function loadBooks() {
    $.ajax({
        url: 'http://localhost:8080/api/v1/books',
        type: 'GET',
        dataType: 'json',
        success: function (data) {
            renderBooks(data);
        },
        error: function () {
            alert('Error loading books!');
        }
    });
}

function renderBooks(data, title) {
    let trHTML = '';
    
    if (title) {
        // Thêm title cho search results
        const tableHead = $('#book-table-body').closest('table').find('thead');
        tableHead.before('<tr><td colspan="7" class="text-center"><h5>' + title + '</h5></td></tr>');
    }
    
    $.each(data, function (i, item) {
        let actionButtons = '<a href="/books/id/' + item.id + '" class="btn btn-info btn-sm"><i class="fas fa-eye me-1"></i>View</a> ';
        
        // Kiểm tra nếu user là ADMIN
        if (currentUserRoles.includes('ROLE_ADMIN')) {
            actionButtons += '<a href="/books/edit/' + item.id + '" class="btn btn-primary btn-sm"><i class="fas fa-edit me-1"></i>Edit</a> ' +
                '<button class="btn btn-danger btn-sm" onclick="apiDeleteBook(' + item.id + ')"><i class="fas fa-trash me-1"></i>Delete</button> ';
        }
        
        // Add to cart cho tất cả user
        actionButtons += '<button class="btn btn-success btn-sm" onclick="addToCart(' + item.id + ', \'' + item.title.replace(/'/g, "\\'") + '\', ' + item.price + ', \'' + (item.imageUrl || '').replace(/'/g, "\\'") + '\')"><i class="fas fa-cart-plus me-1"></i>Add to cart</button>';

        // Image URL or placeholder
        let imageHtml = '<img src="' + (item.imageUrl || 'data:image/svg+xml,%3Csvg xmlns=%22http://www.w3.org/2000/svg%22 width=%2250%22 height=%2270%22%3E%3Crect fill=%22%23ddd%22 width=%2250%22 height=%2270%22/%3E%3Ctext x=%2250%25%22 y=%2250%25%22 fill=%22%23999%22 text-anchor=%22middle%22 dy=%22.3em%22 font-size=%2212%22%3ENo Image%3C/text%3E%3C/svg%3E') + '" alt="Book Cover" class="book-cover-thumbnail" style="max-height:70px;max-width:50px;object-fit:cover;">';

        trHTML += '<tr id="book-' + item.id + '">' +
            '<td>' + imageHtml + '</td>' +
            '<td>' + item.id + '</td>' +
            '<td>' + item.title + '</td>' +
            '<td>' + item.author + '</td>' +
            '<td>$' + item.price + '</td>' +
            '<td>' + (item.category ? item.category : 'N/A') + '</td>' +
            '<td>' + actionButtons + '</td>' +
            '</tr>';
    });
    
    if (data.length === 0) {
        trHTML = '<tr><td colspan="7" class="text-center text-muted">No books found</td></tr>';
    }
    
    $('#book-table-body').html(trHTML);
}


function apiDeleteBook(id) {
    if (confirm('Are you sure you want to delete this book?')) {
        $.ajax({
            url: 'http://localhost:8080/api/v1/books/' + id,
            type: 'DELETE',
            success: function () {
                alert('Book deleted successfully!');
                $('#book-' + id).remove();
            },
            error: function (xhr) {
                if (xhr.status === 403) {
                    alert('You do not have permission to delete this book!');
                } else {
                    alert('Error deleting book! Check your permissions.');
                }
            }
        });
    }
    
}
function addToCart(id, name, price, imageUrl) {
    // Tạo một Form tạm thời
    const form = document.createElement('form');
    form.method = 'POST';
    form.action = '/books/add-to-cart'; // Đường dẫn phải khớp với @PostMapping của bạn

    // Thêm các tham số vào Form
    const params = { id, name, price, quantity: 1, imageUrl };
    
    for (const key in params) {
        const input = document.createElement('input');
        input.type = 'hidden';
        input.name = key;
        input.value = params[key];
        form.appendChild(input);
    }

    // Quan trọng: Thêm CSRF Token nếu Security của Minh đang bật
    // Nếu Minh đã tắt CSRF cho /books/** thì bỏ đoạn này đi
    const csrfToken = document.querySelector('input[name="_csrf"]')?.value;
    if (csrfToken) {
        const csrfInput = document.createElement('input');
        csrfInput.type = 'hidden';
        csrfInput.name = '_csrf';
        csrfInput.value = csrfToken;
        form.appendChild(csrfInput);
    }

    document.body.appendChild(form);
    form.submit(); // Thực hiện chuyển hướng giống như nút bấm cũ
}