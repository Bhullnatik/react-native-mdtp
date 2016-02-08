/**
 * @providesModule react-native-mdtp
 */
const { RNDateTimePicker } = require('react-native').NativeModules;

module.exports = {
  showDatePicker(options = {}) {
    return RNDateTimePicker.showDatePicker(options);
  },
  showTimePicker(options = {}) {
    return RNDateTimePicker.showTimePicker(options);
  },
};
