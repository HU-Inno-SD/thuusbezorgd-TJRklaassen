export class DishService {
    constructor() {
    }

    getDishes(){
        return fetch("/dishes").then(r => r.json());
    }
}
