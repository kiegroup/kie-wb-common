const { override, removeModuleScopePlugin, addWebpackResolve } = require('customize-cra');

module.exports = override(
  removeModuleScopePlugin(),
  addWebpackResolve({ symlinks: false })
);
