const domUtil = {
  buildAminSidebarItemId: (name) => {
    return 'ADMIN_SIDEBAR_' + name;
  },
  buildRowId: (name, index) => {
    return `${name}-row-${index}`;
  }
};

export default domUtil;
