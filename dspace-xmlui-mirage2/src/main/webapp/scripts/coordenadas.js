function obtenCoordenadas(urlBase,param) {

    var page;
    var url = urlBase + "/coordenadas.html?fieldid=" + param;

    page = window.open(url, 'popupmapa', 'toolbar=0,scrollbars=1,location=0,statusbar=0,menubar=0,resizable=1,width=850,height=650');
}
