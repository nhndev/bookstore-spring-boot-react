import moment from 'moment';

const DATE_TIME_FORMAT = 'DD/MM/YYYY HH:mm:ss';

const dateUtil = {
  formatDateTime: (value) => {
    if (!value) return '—';
    const m = moment(value);
    return m.isValid() ? m.format(DATE_TIME_FORMAT) : '—';
  },
};

export default dateUtil;
