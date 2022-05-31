$('#navbar-admin').hide()
$('#navbar-rider').hide()
$('#navbar-customer').hide()

accountRole = getAccountRole();

if (accountRole === 'none')
    window.location.href = 'login.html';

$('#navbar-' + accountRole).show()

function getAccountRole() {
    // TODO prototype, fill with implementation
    let urlParams = new URLSearchParams(window.location.search);
    return urlParams.has('role') ? urlParams.get('role') : 'none';
}