// Handles fetching, rendering, searching, add, edit, delete for users
const API_URL = 'http://localhost:8080/api/users';
let users = [];

async function fetchUsers() {
    console.log('📡 Fetching users from:', API_URL);
    try {
        const res = await fetch(API_URL);
        console.log('🧾 Fetch Response:', res);
        users = await res.json();
        console.log('✅ Users fetched:', users);
        renderUsers(users);
    } catch (err) {
        console.error('❌ Error fetching users:', err);
    }
}

function getField(o, ...keys) {
    if (!o) return null;
    for (const k of keys) {
        if (o[k] !== undefined && o[k] !== null) return o[k];
    }
    return null;
}

function renderUsers(userList) {
    console.log('🎨 Rendering users:', userList.length);
    const tbody = document.getElementById('userTableBody');
    tbody.innerHTML = '';
    userList.forEach((user, idx) => {
        const uid = getField(user, 'userId', 'user_id', 'id');
        tbody.innerHTML += `
        <tr>
            <td class="px-4 py-4 w-8 text-center">
                <input type="checkbox" class="user-checkbox" data-id="${uid}" />
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
    console.log('🧩 Attaching row events...');
    const deleteBtn = document.getElementById('deleteUserBtn');
    const checkboxes = document.querySelectorAll('.user-checkbox');
    const selectAll = document.getElementById('selectAllUsers');

    checkboxes.forEach(cb => {
        cb.addEventListener('change', () => {
            deleteBtn.disabled = !Array.from(checkboxes).some(c => c.checked);
            console.log('✅ Checkbox changed. Delete button state:', !deleteBtn.disabled);
        });
    });

    if (selectAll) {
        selectAll.addEventListener('change', function () {
            checkboxes.forEach(cb => { cb.checked = selectAll.checked; });
            deleteBtn.disabled = !selectAll.checked;
            console.log('🔘 Select all toggled:', selectAll.checked);
        });
    }

    document.querySelectorAll('.edit-user-btn').forEach(btn => {
        btn.addEventListener('click', function () {
            const idx = btn.getAttribute('data-idx');
            console.log('✏️ Edit button clicked for user index:', idx);
            openEditModal(idx);
        });
    });
}

function openEditModal(idx) {
    const user = users[idx];
    if (!user) return;
    console.log('📝 Opening edit modal for user:', user);
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
    console.log('🚀 Page loaded, initializing...');
    fetchUsers();

    try {
        const loggedUser = JSON.parse(localStorage.getItem('loggedUser') || 'null');
        console.log('👤 Logged-in user from localStorage:', loggedUser);
        const role = loggedUser && loggedUser.role ? String(loggedUser.role).toUpperCase() : null;
        if (!loggedUser || (role !== 'ADMIN' && role !== 'HR')) {
            const addBtn = document.getElementById('addUserBtn');
            if (addBtn) addBtn.style.display = 'none';
            console.log('🔒 Hiding Add button for non-admin/HR role');
        }
    } catch (e) {
        console.error('⚠️ Error parsing logged user:', e);
    }

    document.getElementById('addUserForm').addEventListener('submit', async function (e) {
        e.preventDefault();
        console.log('🆕 Form submitted for Add/Edit user');
        const idx = document.getElementById('editUserIndex').value;
        const userData = {
            name: this.name.value,
            email: this.email.value,
            password: this.password.value,
            role: this.role.value,
            status: this.status.value
        };
        console.log('📦 Form data:', userData);
        showProgress();

        let res, data;
        if (idx === '') {
            console.log('➕ Creating new user...');
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
            console.log('📤 Sending Create Request:', { headers, userData });

            res = await fetch(API_URL, { method: 'POST', headers, body: JSON.stringify(userData) });
            const bodyText = await res.text();
            console.log('📨 Create Response:', res.status, bodyText);

            if (res.status === 201) {
                showAlert('User created successfully!', true);
            } else {
                showAlert(bodyText || `Error: ${res.status}`, false);
                hideProgress();
                return;
            }
        } else {
            console.log('✏️ Updating existing user index:', idx);
            const headers = { 'Content-Type': 'application/json' };
            try {
                const lu = JSON.parse(localStorage.getItem('loggedUser')) || null;
                const uid = getField(lu, 'userId', 'user_id', 'id');
                if (uid) headers['X-User-Id'] = Number(uid);
                console.log('📤 Update headers:', headers);
            } catch (e) { console.error('⚠️ Error reading loggedUser:', e); }

            const targetId = getField(users[idx], 'userId', 'user_id', 'id');
            console.log('🎯 Updating user with ID:', targetId);

            res = await fetch(`${API_URL}/${targetId}`, { method: 'PUT', headers, body: JSON.stringify(userData) });
            data = await res.text();
            console.log('📨 Update Response:', res.status, data);

            if (res.status === 200) {
                showAlert('User updated successfully!', true);
            } else {
                showAlert(data || 'Failed to update user', false);
                hideProgress();
                return;
            }
        }

        hideProgress();
        document.getElementById('addUserModal').classList.add('hidden');
        document.getElementById('addUserOverlay').classList.add('hidden');
        fetchUsers();
    });

    document.getElementById('deleteUserBtn').addEventListener('click', async function () {
        console.log('🗑️ Delete button clicked');
        const ids = Array.from(document.querySelectorAll('.user-checkbox:checked')).map(cb => Number(cb.getAttribute('data-id')));
        console.log('🧾 IDs to delete:', ids);
        if (ids.length === 0) return;

        showProgress();
        const headers = { 'Content-Type': 'application/json' };
        try {
            const lu = JSON.parse(localStorage.getItem('loggedUser')) || null;
            const uid = getField(lu, 'userId', 'user_id', 'id');
            if (uid) headers['X-User-Id'] = Number(uid);
            console.log('📤 Delete headers:', headers);
        } catch (e) { console.error('⚠️ Error reading loggedUser for delete:', e); }

        const res = await fetch(API_URL, { method: 'DELETE', headers, body: JSON.stringify(ids) });
        const data = await res.text();
        console.log('📨 Delete Response:', res.status, data);

        if (res.status === 200 || res.status === 206) {
            showAlert('User(s) deleted successfully!', true);
        } else {
            showAlert(data || 'Failed to delete user(s)', false);
            hideProgress();
            return;
        }

        hideProgress();
        fetchUsers();
    });

    document.querySelector('input[placeholder="Search by name or email"]').addEventListener('input', function () {
        const val = this.value.toLowerCase();
        console.log('🔍 Searching for:', val);
        renderUsers(users.filter(u => u.name.toLowerCase().includes(val) || u.email.toLowerCase().includes(val)));
    });

    document.getElementById('closeAlertModal').addEventListener('click', function () {
        console.log('🚪 Closing alert modal');
        document.getElementById('alertModal').classList.add('hidden');
    });

    document.getElementById('alertModal').addEventListener('click', function (e) {
        if (e.target === this) {
            console.log('🖱️ Click outside alert modal, closing...');
            this.classList.add('hidden');
        }
    });
});

function showProgress() {
    console.log('⏳ Showing progress overlay');
    document.getElementById('progressOverlay').classList.remove('hidden');
}

function hideProgress() {
    console.log('✅ Hiding progress overlay');
    document.getElementById('progressOverlay').classList.add('hidden');
}

function showAlert(msg, success) {
    console.log('⚡ Alert:', msg, 'Success:', success);
    const alertModal = document.getElementById('alertModal');
    const alertMsg = document.getElementById('alertModalMsg');
    alertMsg.textContent = msg;
    alertMsg.className = 'text-base ' + (success ? 'text-green-600' : 'text-red-600');
    alertModal.classList.remove('hidden');
}
// ✅ Ensure alert close button works properly
document.addEventListener("DOMContentLoaded", function () {
    const closeBtn = document.getElementById("closeAlertModal");
    const alertModal = document.getElementById("alertModal");

    if (closeBtn && alertModal) {
        closeBtn.addEventListener("click", function (e) {
            console.log("❌ Close button clicked, hiding alert modal");
            alertModal.classList.add("hidden");
        });
    }
});
