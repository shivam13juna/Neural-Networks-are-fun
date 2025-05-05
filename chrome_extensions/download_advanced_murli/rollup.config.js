import resolve from '@rollup/plugin-node-resolve';
import commonjs from '@rollup/plugin-commonjs';
import json from '@rollup/plugin-json';
import copy from 'rollup-plugin-copy';

export default [
  {
    input: 'src/service_worker.js',
    output: {
      file: 'dist/service_worker.js',
      format: 'esm',
      sourcemap: true
    },
    plugins: [
      resolve(),
      commonjs(),
      json(),
      copy({
        targets: [
          { src: 'manifest.json', dest: 'dist' },
          { src: 'icons/*', dest: 'dist/icons' }
        ],
        copyOnce: true
      })
    ]
  }
];
