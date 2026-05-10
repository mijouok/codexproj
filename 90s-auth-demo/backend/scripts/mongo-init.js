/**
 * MongoDB initialization script generated from current Spring Data entities.
 *
 * Usage:
 *   mongosh "mongodb://localhost:27017/alumni_demo" ./scripts/mongo-init.js
 * or
 *   mongosh --file ./scripts/mongo-init.js
 */

const collections = [
  "users",
  "roles",
  "user_roles",
  "refresh_tokens",
  "spaces",
  "memberships",
  "space_invite_codes",
];

function ensureCollection(name) {
  const existing = db.getCollectionNames();
  if (!existing.includes(name)) {
    db.createCollection(name);
    print(`created collection: ${name}`);
  } else {
    print(`collection exists: ${name}`);
  }
}

collections.forEach(ensureCollection);

function sameKeySpec(left, right) {
  const leftKeys = Object.keys(left);
  const rightKeys = Object.keys(right);
  if (leftKeys.length !== rightKeys.length) {
    return false;
  }
  for (const key of leftKeys) {
    if (!Object.prototype.hasOwnProperty.call(right, key)) {
      return false;
    }
    if (left[key] !== right[key]) {
      return false;
    }
  }
  return true;
}

function migrateConflictingIndex(collection, keySpec, canonicalOptions) {
  const indexes = collection.getIndexes();
  for (const index of indexes) {
    if (!sameKeySpec(index.key, keySpec)) {
      continue;
    }
    const sameName = index.name === canonicalOptions.name;
    const sameUnique = Boolean(index.unique) === Boolean(canonicalOptions.unique);
    const sameSparse = Boolean(index.sparse) === Boolean(canonicalOptions.sparse);
    if (sameName && sameUnique && sameSparse) {
      return;
    }
    print(`dropping conflicting index: ${index.name}`);
    collection.dropIndex(index.name);
  }
}

function ensureCanonicalIndex(collection, keySpec, options) {
  migrateConflictingIndex(collection, keySpec, options);
  collection.createIndex(keySpec, options);
}

ensureCanonicalIndex(
  db.users,
  { email: 1 },
  { name: "email_1", unique: true, sparse: true }
);
ensureCanonicalIndex(
  db.users,
  { phone: 1 },
  { name: "phone_1", unique: true, sparse: true }
);

ensureCanonicalIndex(db.roles, { name: 1 }, { name: "name_1", unique: true });

ensureCanonicalIndex(
  db.user_roles,
  { userId: 1, roleId: 1, scopeType: 1, scopeId: 1 },
  { name: "uk_user_roles_assignment", unique: true }
);

ensureCanonicalIndex(db.refresh_tokens, { userId: 1 }, { name: "userId_1" });
ensureCanonicalIndex(
  db.refresh_tokens,
  { tokenHash: 1 },
  { name: "tokenHash_1", unique: true }
);

ensureCanonicalIndex(db.spaces, { slug: 1 }, { name: "slug_1", unique: true });

ensureCanonicalIndex(
  db.memberships,
  { spaceId: 1, userId: 1 },
  { name: "uk_membership_space_user", unique: true }
);

ensureCanonicalIndex(
  db.space_invite_codes,
  { code: 1 },
  { name: "code_1", unique: true }
);

print("MongoDB schema initialization completed.");
