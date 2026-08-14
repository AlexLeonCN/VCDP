const HEX_BODY_REGEX = /^[0-9A-F]+$/i;

export function stripHexPrefix(value) {
  const raw = String(value ?? '').trim();
  if (!raw) {
    return '';
  }
  return /^0x/i.test(raw) ? raw.slice(2) : raw;
}

export function sanitizeHexBody(value) {
  return stripHexPrefix(value)
    .replace(/[^0-9a-fA-F]/g, '')
    .toUpperCase();
}

export function toHexPayload(value) {
  const body = sanitizeHexBody(value);
  return body ? `0x${body}` : '';
}

export function isValidHex(value) {
  return HEX_BODY_REGEX.test(stripHexPrefix(value));
}

export function isPositiveHex(value) {
  if (!isValidHex(value)) {
    return false;
  }
  try {
    return BigInt(`0x${stripHexPrefix(value)}`) > 0n;
  } catch (error) {
    return false;
  }
}
