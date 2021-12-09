import {ipcMain} from 'electron'

ipcMain.handle("dev-tools", event => {
    if (event.sender.isDevToolsOpened()) {
        event.sender.closeDevTools();
    } else {
        event.sender.openDevTools();
    }
});

ipcMain.handle("load-templates", async () => {
    let templates = [{id: 1, name: "节点模板1"}, {id: 2, name: "节点模板2"}];
    return new Promise(resolve => resolve(templates));
});

ipcMain.handle("load-trees", async () => {
    let trees = [
        {
            id: 1,
            name: "测试1",
            tid: 1,
            children: [
                {id: 2, tid: 1},
                {
                    id: 3, tid: 1,
                    children: [
                        {
                            id: 5, tid: 1,
                            children: [
                                {
                                    id: 6, tid: 1,
                                    children: [
                                        {
                                            id: 7, tid: 1,
                                            children: [
                                                {
                                                    id: 8, tid: 1,
                                                    children: [
                                                        {id: 9, tid: 1},
                                                    ]
                                                },
                                            ]
                                        },
                                    ]
                                },
                            ]
                        },
                    ]
                },
                {id: 4, tid: 1},
            ]
        },
        {
            id: 2,
            name: "测试2",
            tid: 1,
            children: [
                {id: 3, tid: 2},
            ]
        }
    ];
    return new Promise(resolve => resolve(trees));
});


ipcMain.handle("save-tree", (event, tree) => {
    console.log(JSON.stringify(tree));
});