// src/modules/admin/utils/refundRuleUtils.js

export const sortRules = (rules) =>
  [...rules].sort(
    (a, b) => a.minDaysBeforeDeparture - b.minDaysBeforeDeparture,
  );

export const formatDayRange = (rule) => {
  const { minDaysBeforeDeparture: min, maxDaysBeforeDeparture: max } = rule;

  if (max === null || max === undefined) {
    return `Từ ${min} ngày trở lên`;
  }

  if (min === max) {
    return `${min} ngày`;
  }

  return `${min} – ${max} ngày`;
};

// Trả về danh sách các cặp luật bị chồng lấn khoảng ngày
export const findOverlaps = (rules, ignoreId = null) => {
  const list = rules.filter((rule) => rule.id !== ignoreId);
  const overlaps = [];

  for (let i = 0; i < list.length; i++) {
    for (let j = i + 1; j < list.length; j++) {
      const a = list[i];
      const b = list[j];

      const aMax = a.maxDaysBeforeDeparture ?? Infinity;
      const bMax = b.maxDaysBeforeDeparture ?? Infinity;

      if (
        a.minDaysBeforeDeparture <= bMax &&
        b.minDaysBeforeDeparture <= aMax
      ) {
        overlaps.push([a, b]);
      }
    }
  }

  return overlaps;
};

// Kiểm tra một khoảng mới có chồng lấn với các luật hiện có không
export const hasOverlapWith = (rules, candidate, ignoreId = null) => {
  const cMax = candidate.maxDaysBeforeDeparture ?? Infinity;

  return rules
    .filter((rule) => rule.id !== ignoreId)
    .some((rule) => {
      const rMax = rule.maxDaysBeforeDeparture ?? Infinity;

      return (
        candidate.minDaysBeforeDeparture <= rMax &&
        rule.minDaysBeforeDeparture <= cMax
      );
    });
};
