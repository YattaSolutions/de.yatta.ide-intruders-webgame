const fs = require("fs");
const path = require("path");
var express = require("express");
var app = express();
var http = require("http").Server(app);

// fs.copyFileSync(path.join(__dirname, '../../shop-ui/dist/libs/checkout-drop-in/checkout-drop-in.umd.min.js'), 'public/checkout-drop-in.umd.min.js');

//start HTTP Server
http.listen(62348, function () {
  console.log("http://localhost:62348");
});

app.use(express.static("public"));
//on User Connection to HTTP Server send Website
app.get("/", function (req, res) {
  res.status(301).redirect("vendor-login/");
});
