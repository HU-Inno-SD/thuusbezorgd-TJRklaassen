export class OrderService {
    constructor() {
    }

    getOrders() {
        return fetch("/orders").then(r => r.json()).then(os => {
            console.log(os);
            return os;
        });
    }

    async postOrder(order) {

        let token = await this.getCsrfToken();

        let headers = {
            "Content-Type": "application/json"
        }
        headers[token.headerName] = token.token;

        let result = await fetch("/orders", {
            method: "POST",
            headers,
            body: JSON.stringify(order)
        }).then(r => {
            if(r.status===201){
                return r.json();
            }else{
                throw new Error("Daar ging iets mis " + r.status + " " + r.statusText)
            }
        });

        return result;
    }


    getCsrfToken() {
        return fetch('/csrf').then(r => r.json());
    }
}
