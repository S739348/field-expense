// Handles fetching, rendering, searching, add, edit, delete for users
const API_URL = 'http://localhost:8080/api/users';

let users = [];

async function fetchUsers() {
    const res = await fetch(API_URL);
    users = await res.json();
    renderUsers(users);
}

function renderUsers(userList) {
    const tbody = document.getElementById('userTableBody');
    tbody.innerHTML = '';
    userList.forEach((user, idx) => {
        tbody.innerHTML += `
        <tr>
            <td class="px-4 py-4 w-8 text-center">
                <input type="checkbox" class="user-checkbox" data-id="${user.user_id}" />
            </td>
            <td class="whitespace-nowrap px-6 py-4">
                <div class="text-sm font-medium text-gray-900 dark:text-white">${user.name}</div>
            </td>
            <td class="hidden whitespace-nowrap px-6 py-4 text-sm text-gray-500 dark:text-gray-400 @[768px]:table-cell">${user.email}</td>
            <td class="hidden whitespace-nowrap px-6 py-4 text-sm text-gray-500 dark:text-gray-400 @[992px]:table-cell">${user.role}</td>
            <td class="hidden whitespace-nowrap px-6 py-4 @[1200px]:table-cell">
                <span class="inline-flex items-center rounded-full ${user.status === 'active' ? 'bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-300' : 'bg-red-100 text-red-800 dark:bg-red-900 dark:text-red-300'} px-2.5 py-0.5 text-xs font-medium">${user.status.charAt(0).toUpperCase() + user.status.slice(1)}</span>
            </td>
            <td class="whitespace-nowrap px-6 py-4 text-center text-sm font-medium">
                <button class="edit-user-btn text-primary hover:text-primary/80" data-idx="${idx}">Edit</button>
            </td>
        </tr>`;
    });
    attachRowEvents();
}

function attachRowEvents() {
    // Enable delete button if any checked
    const deleteBtn = document.getElementById('deleteUserBtn');
    const checkboxes = document.querySelectorAll('.user-checkbox');
    const selectAll = document.getElementById('selectAllUsers');
    checkboxes.forEach(cb => {
        cb.addEventListener('change', () => {
            deleteBtn.disabled = !Array.from(checkboxes).some(c => c.checked);
        });
    });
    if (selectAll) {
        selectAll.addEventListener('change', function () {
            checkboxes.forEach(cb => { cb.checked = selectAll.checked; });
            deleteBtn.disabled = !selectAll.checked;
        });
    }
    // Edit
    document.querySelectorAll('.edit-user-btn').forEach(btn => {
        btn.addEventListener('click', function () {
            const idx = btn.getAttribute('data-idx');
            openEditModal(idx);
        });
    });
}

function openEditModal(idx) {
    const user = users[idx];
    if (!user) return;
    document.getElementById('userModalTitle').textContent = 'Edit User';
    document.querySelector('input[name="name"]').value = user.name;
    document.querySelector('input[name="email"]').value = user.email;
    document.querySelector('input[name="password"]').value = user.password;
    document.querySelector('select[name="role"]').value = user.role;
    document.querySelector('select[name="status"]').value = user.status;
    document.getElementById('editUserIndex').value = idx;
    document.getElementById('addUserModal').classList.remove('hidden');
    document.getElementById('addUserOverlay').classList.remove('hidden');
}

document.addEventListener('DOMContentLoaded', function () {
    fetchUsers();
    // Hide Add User button if logged user is not ADMIN or HR
    try {
        const loggedUser = JSON.parse(localStorage.getItem('loggedUser') || 'null');
        if (!loggedUser || (loggedUser.role !== 'ADMIN' && loggedUser.role !== 'HR')) {
            const addBtn = document.getElementById('addUserBtn');
            if (addBtn) addBtn.style.display = 'none';
        }
    } catch (e) { }
    // Add User
    document.getElementById('addUserForm').addEventListener('submit', async function (e) {
        e.preventDefault();
        const idx = document.getElementById('editUserIndex').value;
        const userData = {
            name: this.name.value,
            email: this.email.value,
            password: this.password.value,
            role: this.role.value,
            status: this.status.value
        };
        showProgress();
        let res, data;
        if (idx === '') {
            // Create
            const headers = { 'Content-Type': 'application/json' };
            try { const lu = JSON.parse(localStorage.getItem('loggedUser')); if (lu) headers['X-User-Id'] = lu.user_id; } catch(e){}
            res = await fetch(API_URL, { method: 'POST', headers, body: JSON.stringify(userData) });
            data = await res.text();
            if (res.status === 201) {
                showAlert('User created successfully!', true);
            } else {
                showAlert(data || 'Failed to create user', false);
                return hideProgress();
            }
        } else {
            // Update
            const headers = { 'Content-Type': 'application/json' };
            try { const lu = JSON.parse(localStorage.getItem('loggedUser')); if (lu) headers['X-User-Id'] = lu.user_id; } catch(e){}
            res = await fetch(`${API_URL}/${users[idx].user_id}`, { method: 'PUT', headers, body: JSON.stringify(userData) });
            data = await res.text();
            if (res.status === 200) {
                showAlert('User updated successfully!', true);
            } else {
                showAlert(data || 'Failed to update user', false);
                return hideProgress();
            }
        }
        hideProgress();
        document.getElementById('addUserModal').classList.add('hidden');
        document.getElementById('addUserOverlay').classList.add('hidden');
        fetchUsers();
    });
    // Delete
    document.getElementById('deleteUserBtn').addEventListener('click', async function () {
        const ids = Array.from(document.querySelectorAll('.user-checkbox:checked')).map(cb => Number(cb.getAttribute('data-id')));
        if (ids.length === 0) return;
        showProgress();
        const headers = { 'Content-Type': 'application/json' };
        try { const lu = JSON.parse(localStorage.getItem('loggedUser')); if (lu) headers['X-User-Id'] = lu.user_id; } catch(e){}
        const res = await fetch(API_URL, { method: 'DELETE', headers, body: JSON.stringify(ids) });
        const data = await res.text();
        if (res.status === 200 || res.status === 206) {
            showAlert('User(s) deleted successfully!', true);
        } else {
            showAlert(data || 'Failed to delete user(s)', false);
            return hideProgress();
        }
        hideProgress();
        fetchUsers();
    });
    // Search
    document.querySelector('input[placeholder="Search by name or email"]').addEventListener('input', function () {
        const val = this.value.toLowerCase();
        renderUsers(users.filter(u => u.name.toLowerCase().includes(val) || u.email.toLowerCase().includes(val)));
    });
    // Alert modal close
    document.getElementById('closeAlertModal').addEventListener('click', function () {
        document.getElementById('alertModal').classList.add('hidden');
    });
    // Hide alert on click outside
    document.getElementById('alertModal').addEventListener('click', function (e) {
        if (e.target === this) this.classList.add('hidden');
    });
});

function showProgress() {
    document.getElementById('progressOverlay').classList.remove('hidden');
}
function hideProgress() {
    document.getElementById('progressOverlay').classList.add('hidden');
}
function showAlert(msg, success) {
    const alertModal = document.getElementById('alertModal');
    const alertMsg = document.getElementById('alertModalMsg');
    alertMsg.textContent = msg;
    alertMsg.className = 'text-base ' + (success ? 'text-green-600' : 'text-red-600');
    alertModal.classList.remove('hidden');
}
