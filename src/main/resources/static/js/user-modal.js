// user-modal.js
// Handles opening and closing the Add User modal and form submission

document.addEventListener('DOMContentLoaded', function () {
    const addUserBtn = document.getElementById('addUserBtn');
    const modal = document.getElementById('addUserModal');
    const closeModalBtn = document.getElementById('closeAddUserModal');
    const overlay = document.getElementById('addUserOverlay');

    if (addUserBtn && modal && closeModalBtn && overlay) {
        addUserBtn.addEventListener('click', function () {
            modal.classList.remove('hidden');
            overlay.classList.remove('hidden');
        });
        closeModalBtn.addEventListener('click', function () {
            modal.classList.add('hidden');
            overlay.classList.add('hidden');
        });
        overlay.addEventListener('click', function () {
            modal.classList.add('hidden');
            overlay.classList.add('hidden');
        });
    }
});

