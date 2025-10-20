// Fetch expense id from query param
function getQueryParam(name) {
    const params = new URLSearchParams(window.location.search);
    return params.get(name);
}

// helper to read fields with multiple possible keys
function getField(o, ...keys) { if (!o) return null; for (const k of keys) if (o[k] !== undefined && o[k] !== null) return o[k]; return null; }

// build headers with X-User-Id only when we have a numeric logged user id
function buildHeaders() {
    const headers = { 'Content-Type': 'application/json' };
    try {
        const lu = JSON.parse(localStorage.getItem('loggedUser')) || null;
        const uid = getField(lu, 'userId', 'user_id', 'id');
        if (uid !== undefined && uid !== null && !Number.isNaN(Number(uid))) {
            headers['X-User-Id'] = String(Number(uid));
        }
    } catch (e) {}
    return headers;
}

async function fetchExpense(id) {
    const res = await fetch(`http://localhost:8080/api/expenses?expenseId=${id}`);
    if (!res.ok) throw new Error('Failed to fetch expense');
    return await res.json();
}

function renderExpense(expense) {
    const container = document.getElementById('expenseContainer');
    container.innerHTML = `
        <div class="grid grid-cols-1 gap-4">
            <div><strong>Employee:</strong> ${expense.user?.name || 'N/A'}</div>
            <div><strong>Amount:</strong> ${expense.amount || 'N/A'}</div>
            <div><strong>Category:</strong> ${expense.category?.name || 'N/A'}</div>
            <div><strong>Description:</strong> ${expense.description || ''}</div>
            <div><strong>Manager Status:</strong> ${expense.managerStatus || 'Pending'}</div>
            <div><strong>HR Status:</strong> ${expense.hrStatus || 'Pending'}</div>
            <div><strong>Finance Status:</strong> ${expense.financeStatus || 'Pending'}</div>
            <div id="actions"></div>
        </div>
    `;

    // Show action buttons based on logged-in user role in localStorage
    function getField(o, ...keys) { if (!o) return null; for (const k of keys) if (o[k] !== undefined && o[k] !== null) return o[k]; return null; }
    const loggedUser = JSON.parse(localStorage.getItem('loggedUser') || 'null');
    const actions = document.getElementById('actions');
    if (!loggedUser) {
        actions.innerHTML = '<div class="text-red-600">Login to approve or take actions</div>';
        return;
    }

    const role = loggedUser && loggedUser.role ? String(loggedUser.role).toUpperCase() : null;
    const userId = getField(loggedUser, 'userId', 'user_id', 'id');

    // Render approval dropdown + save button (floating card style)
    const options = ['Pending', 'Approved', 'Rejected'];
    let current = 'Pending';
    if (role === 'MANAGER') current = expense.managerStatus || 'Pending';
    if (role === 'HR') current = expense.hrStatus || 'Pending';
    if (role === 'FINANCE') current = expense.financeStatus || 'Pending';

    if (['MANAGER', 'HR', 'FINANCE'].includes(role)) {
        actions.innerHTML = `
            <div class="p-4 bg-white dark:bg-gray-800 rounded shadow-lg max-w-sm">
                <div class="mb-2 font-medium">${role} Action</div>
                <select id="approvalSelect" class="w-full p-2 border rounded mb-3">
                    ${options.map(o => `<option value="${o}" ${o===current? 'selected' : ''}>${o}</option>`).join('')}
                </select>
                <div class="flex gap-2">
                    <button id="saveApproval" class="bg-primary text-white px-3 py-1 rounded">Save</button>
                    <button id="cancelApproval" class="border px-3 py-1 rounded">Cancel</button>
                </div>
                <div id="approvalMsg" class="mt-2 text-sm hidden"></div>
            </div>
        `;

        // determine overall statuses and rejection state
        const mgrStatus = String(expense.managerStatus || '').trim().toLowerCase();
        const hrStatus = String(expense.hrStatus || '').trim().toLowerCase();
        const finStatus = String(expense.financeStatus || '').trim().toLowerCase();
        const anyRejected = [mgrStatus, hrStatus, finStatus].some(s => s === 'rejected');

        // Payment status block
        const paidHtml = `<div class="mt-3">Payment status: <strong id="paymentStatus">${(getField(expense,'paymentStatus','payment_status')||'PENDING').toString()}</strong></div>`;
        document.getElementById('actions').insertAdjacentHTML('beforeend', paidHtml);
        const paymentStatusText = String(getField(expense,'paymentStatus','payment_status')||'PENDING').toUpperCase();
        if (role === 'FINANCE') {
            const payBtn = document.createElement('button');
            payBtn.textContent = paymentStatusText === 'PAID' ? 'Paid' : 'Mark Paid';
            payBtn.className = 'ml-2 bg-green-600 text-white px-2 py-1 rounded';
            if (paymentStatusText === 'PAID') payBtn.disabled = true;
            payBtn.addEventListener('click', async () => {
                if (paymentStatusText === 'PAID') { alert('Payment is already marked PAID and cannot be reverted'); return; }
                // If any approver rejected, don't allow marking paid
                if (anyRejected) { alert('Cannot mark paid: expense rejected by one of the approvers'); return; }
                // require finance approved before marking paid
                if (finStatus !== 'approved') { alert('Cannot mark paid until finance approval is completed'); return; }
                try {
                    const headers = buildHeaders();
                    const expId = getField(expense, 'expenseId', 'expense_id', 'id');
                    const res = await fetch('/api/expenses/approve', { method: 'PUT', headers, body: JSON.stringify({ expenseId: expId, approverId: userId, role: 'FINANCE', paymentStatus: 'PAID' }) });
                    const data = await res.json();
                    if (res.ok) {
                        document.getElementById('paymentStatus').textContent = 'PAID';
                        expense.payment_status = 'PAID';
                        payBtn.disabled = true;
                        alert(data.message || 'Payment updated');
                    } else {
                        alert(data.error || 'Failed to update payment');
                    }
                } catch (e) { alert('Network error: ' + e.message); }
            });
            document.getElementById('actions').appendChild(payBtn);
        } else {
            // non-finance: show read-only payment status
            // no button appended
        }

        const approvalSelect = document.getElementById('approvalSelect');
        const saveBtn = document.getElementById('saveApproval');

        // disable controls if already non-pending for this role
        const isLocked = (role === 'MANAGER' && expense.managerStatus && expense.managerStatus.toLowerCase() !== 'pending')
            || (role === 'HR' && expense.hrStatus && expense.hrStatus.toLowerCase() !== 'pending')
            || (role === 'FINANCE' && expense.financeStatus && expense.financeStatus.toLowerCase() !== 'pending');

        if (isLocked) {
            approvalSelect.disabled = true;
            saveBtn.disabled = true;
            const msgEl = document.getElementById('approvalMsg');
            msgEl.textContent = 'This approval is final and cannot be changed.';
            msgEl.classList.remove('hidden');
            msgEl.classList.add('text-gray-600');
        }

        document.getElementById('saveApproval').addEventListener('click', async () => {
            const sel = approvalSelect.value;
            const headers = buildHeaders();
            try {
                const expId = getField(expense, 'expenseId', 'expense_id', 'id');
                const res = await fetch('/api/expenses/approve', { method: 'PUT', headers, body: JSON.stringify({ expenseId: expId, approverId: userId, role: role, status: sel }) });
                const data = await res.json();
                const msgEl = document.getElementById('approvalMsg');
                if (res.ok) {
                    msgEl.textContent = data.message || 'Saved';
                    msgEl.classList.remove('hidden');
                    msgEl.classList.remove('text-red-600');
                    msgEl.classList.add('text-green-600');
                    // reflect change locally
                    if (role === 'MANAGER') expense.managerStatus = sel;
                    if (role === 'HR') expense.hrStatus = sel;
                    if (role === 'FINANCE') expense.financeStatus = sel;
                    // lock after change if not pending
                    if (sel.toLowerCase() !== 'pending') {
                        approvalSelect.disabled = true;
                        saveBtn.disabled = true;
                    }
                } else {
                    msgEl.textContent = data.error || 'Failed';
                    msgEl.classList.remove('hidden');
                    msgEl.classList.remove('text-green-600');
                    msgEl.classList.add('text-red-600');
                }
            } catch (e) {
                alert('Network error: ' + e.message);
            }
        });

        document.getElementById('cancelApproval').addEventListener('click', () => location.reload());
    } else {
        actions.innerHTML = '<div class="text-gray-600">No actions available</div>';
    }
}

async function submitApproval(expenseId, approverId, role, status) {
    const headers = buildHeaders();
    const res = await fetch('http://localhost:8080/api/expenses/approve', {
        method: 'PUT',
        headers: headers,
        body: JSON.stringify({ expenseId, approverId, role, status })
    });
    const data = await res.json();
    if (res.ok) {
        alert(data.message || 'Updated');
        location.reload();
    } else {
        alert(data.error || 'Failed');
    }
}

(async function () {
    const id = getQueryParam('id');
    if (!id) {
        document.getElementById('expenseContainer').textContent = 'No expense id provided in URL';
        return;
    }
    try {
        const res = await fetch(`http://localhost:8080/api/expenses/${id}`);
        if (!res.ok) throw new Error('Not found');
        const expense = await res.json();
        renderExpense(expense);
    } catch (e) {
        document.getElementById('expenseContainer').textContent = 'Failed to load expense: ' + e.message;
    }
})();
