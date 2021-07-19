// noinspection JSUnresolvedFunction,JSJQueryEfficiency

$(document).ready(() => {
    $('.error').hide();
})


const encryptFile = async () => {
    $('.error').hide();
    const file = $('#choose_file');
    const original = $('#eop');
    const confirm = $('#ecp');

    if (file[0].files.length === 0) {
        $('.error').hide();
        swal("Error", "Please select file to encrypt", "error").then(() => {
            $('#sfe-error').show();
        });
        return;
    }

    if (original == null || original.val().trim() === ''
        || confirm == null || confirm.val().trim() === ''
        || original.val() !== confirm.val()) {
        $('.error').hide();
        swal("Error", "Invalid or mismatched passwords", "error").then(() => {
            $('#fek-error').show();
        });
        return;
    }

    $('#enc-form').submit();
}


const decryptFile = () => {
    $('.error').hide();
    const file = $('#dec-file');
    const original = $('#dop');
    const confirm = $('#dcp');

    if (file[0].files.length === 0) {
        $('.error').hide();
        swal("Error", "Please select file to decrypt", "error").then(() => {
            $('#sfd-error').show();
        });
        return;
    }

    if (original == null || original.val().trim() === ''
        || confirm == null || confirm.val().trim() === ''
        || original.val() !== confirm.val()) {
        $('.error').hide();
        swal("Error", "Invalid or mismatched passwords", "error").then(() => {
            $('#fdk-error').show();
        });
        return;
    }

    $('#dec-form').submit();
}