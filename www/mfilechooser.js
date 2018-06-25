var exec = require('cordova/exec');

exports.open = function (arg0, success, error) {
    console.log('Cordova file chooser ...');
    exec(success, error, 'mfilechooser', 'open', [arg0]);
};
