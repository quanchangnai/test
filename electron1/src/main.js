const {app, BrowserWindow} = require('electron');

function createWindow() {
    let window = new BrowserWindow({
        width: 800,
        height: 600,
        autoHideMenuBar: true,
        webPreferences: {
            nodeIntegration: true
        }
    });

    window.loadFile('src/index.html').then(() => console.log('load index.html'))
}

app.whenReady().then(createWindow);

app.on('window-all-closed', function () {
    console.log('window-all-closed');
    app.quit();
});
