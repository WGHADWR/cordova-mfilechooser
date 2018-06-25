var exec = require('cordova/exec');

var mfilechooser = {};

mfilechooser.open = function (arg0, success, error) {
    console.log('Cordova file chooser ...');
    exec(success, error, 'mfilechooser', 'open', [arg0]);
};

module.exports = mfilechooser;
