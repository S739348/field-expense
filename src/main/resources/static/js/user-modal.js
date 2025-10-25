function showProgress() {
    document.getElementById('progressOverlay').classList.remove('hidden');
}

function hideProgress() {
    document.getElementById('progressOverlay').classList.add('hidden');
}

function showAlert(message, isSuccess) {
    const modal = document.getElementById('alertModal');
    const msg = document.getElementById('alertModalMsg');
    msg.textContent = message;
    modal.classList.remove('hidden');
    
    setTimeout(() => {
        modal.classList.add('hidden');
    }, 3000);
}

document.addEventListener('DOMContentLoaded', function() {
    const addUserBtn = document.getElementById('addUserBtn');
    const addUserModal = document.getElementById('addUserModal');
    const addUserOverlay = document.getElementById('addUserOverlay');
    const closeModal = document.getElementById('closeAddUserModal');
    const cancelBtn = document.getElementById('cancelUserBtn');
    const closeAlert = document.getElementById('closeAlertModal');

    if (addUserBtn) {
        addUserBtn.addEventListener('click', function() {
            document.getElementById('userModalTitle').textContent = 'Add Employee';
            document.getElementById('addUserForm').reset();
            document.getElementById('editUserIndex').value = '';
            if (typeof loadManagers === 'function') {
                loadManagers();
            }
            addUserModal.classList.remove('hidden');
            addUserOverlay.classList.remove('hidden');
        });
    }

    if (closeModal) {
        closeModal.addEventListener('click', function() {
            addUserModal.classList.add('hidden');
            addUserOverlay.classList.add('hidden');
        });
    }

    if (cancelBtn) {
        cancelBtn.addEventListener('click', function() {
            addUserModal.classList.add('hidden');
            addUserOverlay.classList.add('hidden');
        });
    }

    if (closeAlert) {
        closeAlert.addEventListener('click', function() {
            document.getElementById('alertModal').classList.add('hidden');
        });
    }

    if (addUserOverlay) {
        addUserOverlay.addEventListener('click', function() {
            addUserModal.classList.add('hidden');
            addUserOverlay.classList.add('hidden');
        });
    }
});