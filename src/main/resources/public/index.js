import {DishService} from "./dishService.js"
import {OrderService} from "./orderService.js";
import {OrderComponent} from "./orderComponent.js";

let dishService = new DishService();
let orderService = new OrderService();

customElements.define('orders-overview', OrderComponent(dishService, orderService));
