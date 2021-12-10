import {ipcMain} from 'electron'
import fs from 'fs'
import path from 'path'

ipcMain.handle("dev-tools", event => {
    if (event.sender.isDevToolsOpened()) {
        event.sender.closeDevTools();
    } else {
        event.sender.openDevTools();
    }
});

ipcMain.handle("load-templates", async () => {
    return [{id: 1, name: "节点模板1"}, {id: 2, name: "节点模板2"}];
});

ipcMain.handle("load-trees", async () => {
    return [
        {
            id: 1, name: "测试1", tid: 1, folded: false,
            children: [
                {id: 2, tid: 1, folded: false},
                {
                    id: 3, tid: 1, folded: false,
                    children: [
                        {
                            id: 5, tid: 1, folded: false,
                            children: [
                                {
                                    id: 6, tid: 1, folded: false,
                                    children: [
                                        {
                                            id: 7, tid: 1, folded: false,
                                            children: [
                                                {
                                                    id: 8, tid: 1, folded: false,
                                                    children: [
                                                        {id: 9, tid: 1, folded: false},
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
                {id: 4, tid: 1, folded: false},
            ]
        },
        {
            id: 2, name: "测试2", tid: 1, folded: false,
            children: [
                {id: 3, tid: 2, folded: false},
            ]
        }
    ];
});


ipcMain.handle("save-tree", async (event, tree) => {
    console.log("save-tree:" + fs.constants.F_OK);
    console.log(JSON.stringify(tree));
    let workPath = path.resolve("./");
    return {__dirname, __filename, workPath}
});