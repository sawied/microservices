const path = require("path")

const rules = [
  { test: /\.(worker\.js)(\?.*)?$/,
    use: [
      {
        loader: "worker-loader",
        options: {
          inline: true
        }
      },
      { loader: "babel-loader?retainLines=true" }
    ]
  },
  { test: /\.(css)(\?.*)?$/,
    use: [
      "style-loader",
      "css-loader",
      "postcss-loader"
    ]
  },
  { test: /\.(scss)(\?.*)?$/,
    use: [
      "style-loader",
      "css-loader",
      {
        loader: "postcss-loader",
        options: { sourceMap: true }
      },
      { loader: "sass-loader",
        options: {
          outputStyle: "expanded",
          sourceMap: true,
          sourceMapContents: "true",
          includePaths:[path.resolve(__dirname,"./node_modules/compass-mixins/lib")]
        }
      }
    ]
  }
]

module.exports = require("./make-webpack-config")(rules, {
  _special: {
    separateStylesheets: false,
  },
  devtool: "source-map",
  mode:"development",
  entry: {
    "web-core": [
      "./src/core/index.jsx"
    ],
    "web-components": [
      "./src/style/main.scss"
    ]
  },
  output: {
    pathinfo: true,
    filename: "[name].js",
    library: "[name]",
    libraryTarget: "umd",
    chunkFilename: "[id].js"
  },
  devServer: {
    host:"localhost",
    port: 3200,
    publicPath: "/",
    noInfo: false,
    open:true,
    openPage:"",
    hot: true,
    proxy:{
      '/apis':{
        target:'http://localhost:9000',
        pathRewrite: {'^/apis' : ''}
      }
    },
    after:function(app){console.log("app started success",app)},
    disableHostCheck: true, // for development within VMs
    stats: {
      colors: true
    },
  },
})
