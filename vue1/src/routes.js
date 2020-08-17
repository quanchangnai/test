import Page1 from "./components/Page1";
import Page2 from "./components/Page2";
import Page3 from "./components/Page3";

export default [
    {path: '/', redirect: '/aaa'},
    {path: '/aaa', component: Page1},
    {path: '/bbb', component: Page2},
    {path: '/ccc', component: Page3},
]