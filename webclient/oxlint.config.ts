import { defineConfig } from "oxlint";

export default defineConfig({
  options: {
    typeAware: true,
  },
  plugins: ["react"],
  rules: {
    "eslint/no-unused-vars": ["warn", { caughtErrorsIgnorePattern: "^_" }],
  },
});
