export function isPathActive(pathname, item) {
  if (pathname === item.path) return true;
  if (!item.end && pathname.startsWith(`${item.path}/`)) return true;
  return item.extraPaths?.some(
    (p) => pathname === p || pathname.startsWith(`${p}/`),
  );
}
