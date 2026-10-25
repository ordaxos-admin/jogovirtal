// JogoVirtal original procedural pet. No third-party 3D assets or dependencies.
// Run: node scripts/generate-pet.mjs
import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const base = path.dirname(fileURLToPath(import.meta.url));
const output = path.resolve(base, '../app/src/main/assets/models/pet.glb');
const segments = 16;
const rings = 12;
const binary = [];
const bufferViews = [];
const accessors = [];
const materials = [];
const primitives = [];
let bufferSize = 0;

function pushBytes(bytes, target) {
  const padding = (4 - (bufferSize % 4)) % 4;
  if (padding) { binary.push(Buffer.alloc(padding)); bufferSize += padding; }
  const index = bufferViews.length;
  bufferViews.push({ buffer: 0, byteOffset: bufferSize, byteLength: bytes.length, target });
  binary.push(bytes);
  bufferSize += bytes.length;
  return index;
}

function vecAccessor(values, componentType, type, target, bounds) {
  const typed = componentType === 5126 ? new Float32Array(values) : new Uint16Array(values);
  const bytes = Buffer.from(typed.buffer, typed.byteOffset, typed.byteLength);
  const view = pushBytes(bytes, target);
  const components = type === 'VEC3' ? 3 : 1;
  const entry = { bufferView: view, componentType, count: values.length / components, type };
  if (bounds) { entry.min = bounds.min; entry.max = bounds.max; }
  accessors.push(entry);
  return accessors.length - 1;
}

function rgb(hex) {
  const clean = hex.replace('#', '');
  return [0, 2, 4].map(n => parseInt(clean.slice(n, n + 2), 16) / 255).concat(1);
}

function ellipsoid(name, center, radii, color) {
  const pos = [], norm = [], indices = [];
  const [cx,cy,cz] = center, [rx,ry,rz] = radii;
  for (let i = 0; i <= rings; i++) {
    const theta = i * Math.PI / rings;
    for (let j = 0; j <= segments; j++) {
      const phi = j * Math.PI * 2 / segments;
      const ux = Math.sin(theta) * Math.cos(phi);
      const uy = Math.cos(theta);
      const uz = Math.sin(theta) * Math.sin(phi);
      pos.push(cx + rx * ux, cy + ry * uy, cz + rz * uz);
      const vx = ux / rx, vy = uy / ry, vz = uz / rz;
      const len = Math.hypot(vx, vy, vz);
      norm.push(vx / len, vy / len, vz / len);
    }
  }
  for (let i = 0; i < rings; i++) {
    for (let j = 0; j < segments; j++) {
      const a = i * (segments + 1) + j;
      const b = a + 1;
      const c = a + segments + 1;
      const d = c + 1;
      indices.push(a, b, c, b, d, c);
    }
  }
  const mins = [cx - rx, cy - ry, cz - rz];
  const maxs = [cx + rx, cy + ry, cz + rz];
  const position = vecAccessor(pos, 5126, 'VEC3', 34962, { min: mins, max: maxs });
  const normal = vecAccessor(norm, 5126, 'VEC3', 34962);
  const index = vecAccessor(indices, 5123, 'SCALAR', 34963);
  const material = materials.length;
  materials.push({
    name: name + 'Color',
    doubleSided: true,
    pbrMetallicRoughness: {
      baseColorFactor: rgb(color),
      metallicFactor: 0.0,
      roughnessFactor: 0.9
    }
  });
  primitives.push({ attributes: { POSITION: position, NORMAL: normal }, indices: index, material, mode: 4 });
}

// Soft creature with two ears, two legs, large eyes and a small nose.
ellipsoid('body', [0, 0.26, 0], [0.22, 0.24, 0.19], '#70d6c4');
ellipsoid('belly', [0, 0.26, 0.172], [0.15, 0.17, 0.047], '#f6efe0');
ellipsoid('head', [0, 0.49, 0.06], [0.19, 0.18, 0.17], '#79dfcb');
ellipsoid('left-ear', [-0.125, 0.68, 0.01], [0.068, 0.16, 0.07], '#79dfcb');
ellipsoid('right-ear', [0.125, 0.68, 0.01], [0.068, 0.16, 0.07], '#79dfcb');
ellipsoid('left-inner-ear', [-0.125, 0.69, 0.071], [0.036, 0.105, 0.013], '#ffb4bc');
ellipsoid('right-inner-ear', [0.125, 0.69, 0.071], [0.036, 0.105, 0.013], '#ffb4bc');
ellipsoid('left-eye', [-0.072, 0.515, 0.213], [0.037, 0.044, 0.025], '#25344d');
ellipsoid('right-eye', [0.072, 0.515, 0.213], [0.037, 0.044, 0.025], '#25344d');
ellipsoid('left-sparkle', [-0.082, 0.53, 0.234], [0.01, 0.013, 0.007], '#ffffff');
ellipsoid('right-sparkle', [0.062, 0.53, 0.234], [0.01, 0.013, 0.007], '#ffffff');
ellipsoid('nose', [0, 0.457, 0.239], [0.025, 0.018, 0.015], '#ff8397');
ellipsoid('left-foot', [-0.125, 0.084, 0.1], [0.092, 0.067, 0.122], '#56bca9');
ellipsoid('right-foot', [0.125, 0.084, 0.1], [0.092, 0.067, 0.122], '#56bca9');
ellipsoid('tail', [0, 0.23, -0.22], [0.076, 0.08, 0.14], '#79dfcb');

const gltf = {
  asset: { version: '2.0', generator: 'JogoVirtal procedural pet generator' },
  scene: 0,
  scenes: [{ nodes: [0] }],
  nodes: [{ name: 'Pet', mesh: 0 }],
  meshes: [{ name: 'JogoVirtalPet', primitives }],
  materials,
  accessors,
  bufferViews,
  buffers: [{ byteLength: bufferSize }]
};

const jsonBytes = Buffer.from(JSON.stringify(gltf), 'utf8');
const jsonPad = (4 - (jsonBytes.length % 4)) % 4;
const paddedJson = Buffer.concat([jsonBytes, Buffer.alloc(jsonPad, 0x20)]);
const body = Buffer.concat(binary);
const binPad = (4 - (body.length % 4)) % 4;
const paddedBin = Buffer.concat([body, Buffer.alloc(binPad)]);
const total = 12 + 8 + paddedJson.length + 8 + paddedBin.length;
const glb = Buffer.alloc(total);
glb.writeUInt32LE(0x46546c67, 0);
glb.writeUInt32LE(2, 4);
glb.writeUInt32LE(total, 8);
let offset = 12;
glb.writeUInt32LE(paddedJson.length, offset); offset += 4;
glb.writeUInt32LE(0x4e4f534a, offset); offset += 4;
paddedJson.copy(glb, offset); offset += paddedJson.length;
glb.writeUInt32LE(paddedBin.length, offset); offset += 4;
glb.writeUInt32LE(0x004e4942, offset); offset += 4;
paddedBin.copy(glb, offset);
fs.mkdirSync(path.dirname(output), { recursive: true });
fs.writeFileSync(output, glb);
console.log('Generated original pet GLB:', output, 'size:', glb.length, 'bytes');
