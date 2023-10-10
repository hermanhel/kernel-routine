const defaultTheme = require('tailwindcss/defaultTheme')
/** @type {import('tailwindcss').Config} */
module.exports = {
  // content: process.env.NODE_ENV == 'production' ? ["./public/js/app/main.js"] : ["./src/main.cljs", "./public/js/app/cljs-runtime.js"],
  content: ["./src/main/kernel/*"],
  theme: {
    extend: {},
  },
  plugins: [],
}

