const template = document.createElement('template');
template.innerHTML = `
<style>
div {
   background-color: slategray; 
}
</style>
<div>
    
    <form id="order-form" action="/orders" method="POST">
        <input id="csrf-field" type="hidden" value="" />
        <h2>Gerechten</h2>
        <ul id="dishes"></ul>
        <h2>Adres</h2>
        <ul>
            <li><label for="street">Street</label><input type="text" id="street" name="street" required></li>
            <li><label for="nr">House-number</label><input type="text" id="nr" name="nr" required></li>
            <li><label for="city">City</label><input type="text" id="city" name="city" required></li>
            <li><label for="zip">Zip</label><input type="text" id="zip" name="zip" required></li>
        </ul>
        <button type="submit">Bestellen!</button>
    </form>
</div>
`;

export function OrderComponent(dishService, orderService) {
    return class OrderComponent extends HTMLElement {
        ul;

        constructor() {
            super();
            console.debug('ik word geconstruct');

            this.dishService = dishService;
            this.orderService = orderService;

            this.shadow = this.attachShadow({mode: 'open'});
            this.shadow.appendChild(template.content.cloneNode(true));
            this.ul = this.shadow.querySelector('#dishes');
            this.orderForm = this.shadow.querySelector('#order-form');
            this.csrfInput = this.shadow.querySelector('#csrf-field');

            this.orderForm.addEventListener("submit", async e => {
                e.preventDefault();
                let dishes = this.getSelectedDishes();
                try {
                    await this.orderService.postOrder({
                        dishes: dishes,
                        address: {
                            street: this.orderForm.street.value,
                            nr: this.orderForm.nr.value,
                            city: this.orderForm.city.value,
                            zip: this.orderForm.zip.value,
                        }
                    });
                    alert('bestelling geplaatst!');
                } catch (error) {
                    alert(error);
                }


            })
        }

        getCsrfToken() {
            return fetch('/csrf').then(r => r.json());
        }

        connectedCallback() {
            console.debug('ik word geconnect! En ik ben nu isConnected:', this.isConnected)

            return Promise.all([
                this.getCsrfToken().then(t => {
                    this.csrfInput.name = t.paramName;
                    this.csrfInput.value = t.token;
                }),
                this.refreshDishes().then(() => {
                    console.debug("Dishes refreshed");
                })
            ]).then()
        }

        getSelectedDishes() {
            let checked = this.ul.querySelectorAll("input:checked");

            let dishes = [];
            for (let i of checked) {
                dishes.push(this.items[i.id]);
            }
            return dishes;
        }

        async refreshDishes() {
            this.ul.innerHTML = "";
            this.items = {};

            let dishes = await this.dishService.getDishes()
            for (let d of dishes) {
                let li = document.createElement('li');
                li.innerHTML = `<label for="d-${d.id}">${d.name}</label><input type="checkbox" name="dish" value="${d.id}" id="d-${d.id}">`
                let input = li.querySelector('input');
                this.items[input.id] = d;
                this.ul.appendChild(li);
            }
        }
    }
}
