import test from 'node:test';
import assert from 'node:assert/strict';
import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const petFile = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '../app/src/main/assets/models/pet.glb');

test('procedural GLB has valid container, buffers, references and small payload', () => {
  const glb = fs.readFileSync(petFile);
  assert.equal(glb.readUInt32LE(0), 0x46546c67);
  assert.equal(glb.readUInt32LE(4), 2);
  assert.equal(glb.readUInt32LE(8), glb.length);
  assert.ok(glb.length < 150_000, 'Keep first pet lightweight');
  const jsonLength = glb.readUInt32LE(12);
  assert.equal(glb.readUInt32LE(16), 0x4e4f534a);
  const document = JSON.parse(glb.subarray(20, 20 + jsonLength).toString('utf8'));
  const binHeader = 20 + jsonLength;
  const binLength = glb.readUInt32LE(binHeader);
  assert.equal(glb.readUInt32LE(binHeader + 4), 0x004e4942);
  assert.equal(binHeader + 8 + binLength, glb.length);
  assert.equal(document.asset.version, '2.0');
  assert.ok(document.meshes[0].primitives.length >= 12);
  assert.equal(document.buffers[0].byteLength <= binLength, true);
  for (const bv of document.bufferViews) {
    assert.equal(bv.byteOffset % 4, 0);
    assert.ok(bv.byteOffset + bv.byteLength <= document.buffers[0].byteLength);
  }
  for (const primitive of document.meshes[0].primitives) {
    assert.ok(document.accessors[primitive.attributes.POSITION].count > 0);
    assert.ok(document.accessors[primitive.indices].count > 0);
  }
});
