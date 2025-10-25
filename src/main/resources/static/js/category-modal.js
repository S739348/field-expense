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
    const addCategoryBtn = document.getElementById('addCategoryBtn');
    const addCategoryModal = document.getElementById('addCategoryModal');
    const addCategoryOverlay = document.getElementById('addCategoryOverlay');
    const closeModal = document.getElementById('closeAddCategoryModal');
    const cancelBtn = document.getElementById('cancelCategoryBtn');
    const closeAlert = document.getElementById('closeAlertModal');

    if (addCategoryBtn) {
        addCategoryBtn.addEventListener('click', function() {
            document.getElementById('categoryModalTitle').textContent = 'Add Category';
            document.getElementById('addCategoryForm').reset();
            document.getElementById('editCategoryIndex').value = '';
            addCategoryModal.classList.remove('hidden');
            addCategoryOverlay.classList.remove('hidden');
        });
    }

    if (closeModal) {
        closeModal.addEventListener('click', function() {
            addCategoryModal.classList.add('hidden');
            addCategoryOverlay.classList.add('hidden');
        });
    }

    if (cancelBtn) {
        cancelBtn.addEventListener('click', function() {
            addCategoryModal.classList.add('hidden');
            addCategoryOverlay.classList.add('hidden');
        });
    }

    if (closeAlert) {
        closeAlert.addEventListener('click', function() {
            document.getElementById('alertModal').classList.add('hidden');
        });
    }

    if (addCategoryOverlay) {
        addCategoryOverlay.addEventListener('click', function() {
            addCategoryModal.classList.add('hidden');
            addCategoryOverlay.classList.add('hidden');
        });
    }
});