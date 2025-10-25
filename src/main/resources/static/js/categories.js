const API_URL = 'http://localhost:8080/api/categories';
let categories = [];

async function fetchCategories() {
    console.log('📡 Fetching categories from:', API_URL + '/all');
    try {
        const res = await fetch(API_URL + '/all');
        console.log('🧾 Fetch Response:', res);
        categories = await res.json();
        console.log('✅ Categories fetched:', categories);
        renderCategories(categories);
    } catch (err) {
        console.error('❌ Error fetching categories:', err);
    }
}

function getField(o, ...keys) {
    if (!o) return null;
    for (const k of keys) {
        if (o[k] !== undefined && o[k] !== null) return o[k];
    }
    return null;
}

function renderCategories(categoryList) {
    console.log('🎨 Rendering categories:', categoryList.length);
    const tbody = document.getElementById('categoryTableBody');
    tbody.innerHTML = '';
    categoryList.forEach((category, idx) => {
        const cid = getField(category, 'category_id', 'categoryId', 'id');
        tbody.innerHTML += `
        <tr>
            <td class="px-4 py-4 w-8 text-center">
                <input type="checkbox" class="category-checkbox" data-id="${cid}" />
            </td>
            <td class="whitespace-nowrap px-6 py-4">
                <div class="text-sm font-medium text-gray-900 dark:text-white flex items-center gap-2">
                    <span class="material-symbols-outlined text-primary">category</span>
                    ${category.name}
                </div>
            </td>
            <td class="whitespace-nowrap px-6 py-4 text-center text-sm font-medium">
                <button class="edit-category-btn text-primary hover:text-primary/80" data-idx="${idx}">Edit</button>
            </td>
        </tr>`;
    });
    attachRowEvents();
}

function attachRowEvents() {
    console.log('🧩 Attaching row events...');
    const deleteBtn = document.getElementById('deleteCategoryBtn');
    const checkboxes = document.querySelectorAll('.category-checkbox');
    const selectAll = document.getElementById('selectAllCategories');

    checkboxes.forEach(cb => {
        cb.addEventListener('change', () => {
            deleteBtn.disabled = !Array.from(checkboxes).some(c => c.checked);
            updateSelectedCount();
            console.log('✅ Checkbox changed. Delete button state:', !deleteBtn.disabled);
        });
    });

    if (selectAll) {
        selectAll.addEventListener('change', function () {
            checkboxes.forEach(cb => { 
                cb.checked = selectAll.checked;
                const row = cb.closest('tr');
                if (row) {
                    if (selectAll.checked) {
                        row.classList.add('selected-row');
                    } else {
                        row.classList.remove('selected-row');
                    }
                }
            });
            deleteBtn.disabled = !selectAll.checked;
            updateSelectedCount();
            console.log('🔘 Select all toggled:', selectAll.checked);
        });
    }

    document.querySelectorAll('.edit-category-btn').forEach(btn => {
        btn.addEventListener('click', function () {
            const idx = btn.getAttribute('data-idx');
            console.log('✏️ Edit button clicked for category index:', idx);
            openEditModal(idx);
        });
    });

    // Add row selection highlighting
    checkboxes.forEach(cb => {
        cb.addEventListener('change', function() {
            const row = this.closest('tr');
            if (row) {
                if (this.checked) {
                    row.classList.add('selected-row');
                } else {
                    row.classList.remove('selected-row');
                }
            }
        });
    });
}

function updateSelectedCount() {
    const checkboxes = document.querySelectorAll('#categoryTableBody input[type="checkbox"]:checked');
    const count = checkboxes.length;
    document.getElementById('selectedCount').textContent = count;
    document.getElementById('deleteCategoryBtn').disabled = count === 0;
}

function openEditModal(idx) {
    const category = categories[idx];
    if (!category) return;
    console.log('📝 Opening edit modal for category:', category);
    document.getElementById('categoryModalTitle').textContent = 'Edit Category';
    document.querySelector('input[name="name"]').value = category.name;
    document.getElementById('editCategoryIndex').value = idx;
    document.getElementById('addCategoryModal').classList.remove('hidden');
    document.getElementById('addCategoryOverlay').classList.remove('hidden');
}

document.addEventListener('DOMContentLoaded', function () {
    console.log('🚀 Page loaded, initializing...');
    fetchCategories();

    try {
        const loggedUser = JSON.parse(localStorage.getItem('loggedUser') || 'null');
        console.log('👤 Logged-in user from localStorage:', loggedUser);
        const role = loggedUser && loggedUser.role ? String(loggedUser.role).toUpperCase() : null;
        if (!loggedUser || (role !== 'ADMIN' && role !== 'HR')) {
            const addBtn = document.getElementById('addCategoryBtn');
            if (addBtn) addBtn.style.display = 'none';
            console.log('🔒 Hiding Add button for non-admin/HR role');
        }
    } catch (e) {
        console.error('⚠️ Error parsing logged user:', e);
    }

    // Search functionality
    document.getElementById('searchInput').addEventListener('input', function() {
        const query = this.value.toLowerCase();
        const filtered = categories.filter(cat => 
            cat.name.toLowerCase().includes(query)
        );
        renderCategories(filtered);
    });

    document.getElementById('addCategoryForm').addEventListener('submit', async function (e) {
        e.preventDefault();
        console.log('🆕 Form submitted for Add/Edit category');
        const idx = document.getElementById('editCategoryIndex').value;
        const categoryName = this.name.value.trim();
        
        console.log('📦 Form data:', { name: categoryName });
        showProgress();

        let res, data;
        if (idx === '') {
            console.log('➕ Creating new category...');
            const headers = { 'Content-Type': 'application/json' };
            let uid = null;
            try {
                const lu = JSON.parse(localStorage.getItem('loggedUser')) || null;
                console.log('🧠 LocalStorage loggedUser:', lu);
                uid = lu ? lu.userId: null;
            } catch (e) { uid = null; }

            if (!uid || Number.isNaN(Number(uid))) {
                hideProgress();
                showAlert('Missing loggedUser ID. Login again.', false);
                console.error('🚫 No userId found in localStorage.loggedUser');
                return;
            }

            headers['X-User-Id'] = Number(uid);
            console.log('📤 Sending Create Request:', { headers, names: [categoryName] });

            res = await fetch(API_URL + '/create', { 
                method: 'POST', 
                headers, 
                body: JSON.stringify([categoryName]) 
            });
            const bodyText = await res.text();
            console.log('📨 Create Response:', res.status, bodyText);

            if (res.status === 201) {
                showAlert('Category created successfully!', true);
            } else {
                showAlert(bodyText || `Error: ${res.status}`, false);
                hideProgress();
                return;
            }
        } else {
            console.log('✏️ Updating existing category index:', idx);
            const headers = { 'Content-Type': 'application/json' };
            try {
                const lu = JSON.parse(localStorage.getItem('loggedUser')) || null;
                const uid = getField(lu, 'userId', 'user_id', 'id');
                if (uid) headers['X-User-Id'] = Number(uid);
                console.log('📤 Update headers:', headers);
            } catch (e) { console.error('⚠️ Error reading loggedUser:', e); }

            const targetId = getField(categories[idx], 'category_id', 'categoryId', 'id');
            console.log('🎯 Updating category with ID:', targetId);

            const categoryData = { name: categoryName };
            res = await fetch(`${API_URL}/${targetId}`, { 
                method: 'PUT', 
                headers, 
                body: JSON.stringify(categoryData) 
            });
            data = await res.text();
            console.log('📨 Update Response:', res.status, data);

            if (res.status === 200) {
                showAlert('Category updated successfully!', true);
            } else {
                showAlert(data || 'Failed to update category', false);
                hideProgress();
                return;
            }
        }

        hideProgress();
        document.getElementById('addCategoryModal').classList.add('hidden');
        document.getElementById('addCategoryOverlay').classList.add('hidden');
        fetchCategories();
    });

    document.getElementById('deleteCategoryBtn').addEventListener('click', async function () {
        console.log('🗑️ Delete button clicked');
        const ids = Array.from(document.querySelectorAll('.category-checkbox:checked')).map(cb => Number(cb.getAttribute('data-id')));
        console.log('🧾 IDs to delete:', ids);
        if (ids.length === 0) return;

        if (!confirm(`Are you sure you want to delete ${ids.length} categories?`)) return;

        showProgress();
        const headers = { 'Content-Type': 'application/json' };
        try {
            const lu = JSON.parse(localStorage.getItem('loggedUser')) || null;
            const uid = getField(lu, 'userId', 'user_id', 'id');
            if (uid) headers['X-User-Id'] = Number(uid);
        } catch (e) { console.error('⚠️ Error reading loggedUser:', e); }

        console.log('📤 Sending Delete Request:', { headers, ids });

        try {
            const res = await fetch(API_URL, { 
                method: 'DELETE', 
                headers, 
                body: JSON.stringify(ids) 
            });
            const bodyText = await res.text();
            console.log('📨 Delete Response:', res.status, bodyText);

            if (res.ok) {
                showAlert('Categories deleted successfully!', true);
            } else {
                showAlert(bodyText || 'Failed to delete categories', false);
            }
        } catch (err) {
            console.error('❌ Error deleting categories:', err);
            showAlert('Error deleting categories', false);
        }

        hideProgress();
        fetchCategories();
    });
});