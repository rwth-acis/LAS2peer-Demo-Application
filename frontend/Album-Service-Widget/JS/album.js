/**
 * Simple example showing usage of serviceAPI and widgetAPI.
 * Add multpile widgets to your space.
 * Each one will have a random color.
 * By clicking on a widget it will send an intent to all other widgets.
 * Some widgets will then change their color.
 */
var api = i5.las2peer.jsAPI;
var serverURL="http://localhost:8080/LAS2peerFosdemDemo";
/*
  Check explicitly if gadgets is known, i.e. script is executed in widget environment.
  Allows compatibility as a Web page.
 */

if (typeof gadgets !== "undefined" && gadgets !== null) {
  iwcCallback = function (intent) {
    //listen to intent, wait for action
    if (intent.action === "WIDGET_CLICKED") {
      //react on intent data
      if (intent.data == myId%2)
        setRandomColor();
    }

    };
  iwcManager = new api.IWCManager(iwcCallback);
}

//we don't need to login
var login = new api.Login(api.LoginTypes.NONE);




/*
  Init RequestSender object with uri and login data.
 */

var requestSender = new api.RequestSender(serverURL, login);



$(document).ready(function () {
  init();
});

/*
 * Loads default unfiltered images
 */
var init = function () {
  loadImages("");
  initEvents();
};

var initEvents = function () {
  $(".imageTile").click(clickTile);

  $("#searchButton").click(findImages);

  $("#searchField").keypress(function (e) {
    if (e.which == 13) {
      findImages();
    }
  });
}

/*
 * Requests images matching the pattern in the search field
 */
var findImages = function () {
  var filter = $("#searchField").val();
  if (filter != null)
    loadImages(filter);
  else
    loadImages("");
}

/*
 * Colors a selected tile and reads the id of the image
 */
var clickTile = function () {
  $(this).parent().find(".selected").removeClass("selected");
  $(this).addClass("selected");

  //TODO send selected Id to other widgets!!
  alert($(this).find("img").data("imageid"));
}

/*
 * Loads and displays all images having the pattern in their id
 */
var loadImages = function (filter) {

  var query = "";
  if (typeof filter !== "undefined" && filter !== null && filter.trim().length>0)
    query = "?filter=" + filter;

  var request = new api.Request("get", "images" + query, "", function (data) {
    var jsonArray = jQuery.parseJSON(data)
    $("#imageSelection").empty();
    //TODO limit the amount of loaded images
    if(jsonArray!==null && jsonArray.length>0)
    {
      var requestImage = [];
      for (var i = 0; i < jsonArray.length; i++) {
        //alert(jsonArray[i]);
        var item = jsonArray[i];
        (function (item) {
          requestImage.push(new api.Request("get", "images/" + item, "", function (image) {
            var tile = document.createElement("div");
            tile.className = "imageTile";
            var imgHelper = document.createElement("span");
            imgHelper.className = "imgHelper";
            var img = document.createElement("img");
            img.src = image;
         
            $(img).data("imageid", item);
            tile.appendChild(imgHelper);
            tile.appendChild(img);

            $(tile).click(clickTile);
            $("#imageSelection").append(tile);
          }));
        })(item);
      }
      //TODO check why sometimes there is a 500 Error with async!!!
      requestSender.sendRequestsSync(requestImage, function () { });
    }
  }, function (error) { alert(error);});
  requestSender.sendRequestObj(request);
}

