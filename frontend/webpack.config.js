var path = require('path');

module.exports = {
    entry: './app/index.js',
    output: {
        path: path.resolve(__dirname, "../src/main/resources/static/build"),
        publicPath: '/build',
        filename: 'bundle.js'
    },

    devServer: {
//        publicPath: "/",
        contentBase: "../src/main/resources/static",
        //hot: true,
//        inline: true,
        port: 8080
    },

    module: {
        rules: [
            {
                test: /\.js$/,
                exclude: /node_modules/,
                include: [
                    path.resolve(__dirname, "app")
                ],

                loader: "babel-loader"
            }
        ]
    }
};