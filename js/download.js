(function () {
    $(document).ready(function () {
        $.getJSON("https://api.github.com/repos/hendraanggrian/plano/releases/latest").done(function (json) {
            json.assets.forEach(function (asset) {
                if (asset.name.endsWith("apk")) {
                    $("#download-android").attr("href", asset.browser_download_url);
                } else if (asset.name.endsWith("dmg")) {
                    $("#download-mac").attr("href", asset.browser_download_url);
                } else if (asset.name.endsWith("exe")) {
                    $("#download-windows").attr("href", asset.browser_download_url);
                } else if (asset.name.endsWith("jar")) {
                    $("#download-java").attr("href", asset.browser_download_url);
                }
            });
        });
    });
})();