import http from 'k6/http';
import { check } from 'k6';

export const options = {
    vus: 500,
    duration: '30s',

    thresholds: {
        http_req_failed: ['rate<0.01'],
        http_req_duration: ['p(95)<200'],
    },
};

let shortCode;

export function setup() {

    const response = http.post(
        'http://localhost:8080',
        JSON.stringify({
            originalUrl: 'https://roadmap.sh/java',
            expiresAt: '2027-01-01T00:00:00Z'
        }),
        {
            headers: {
                'Content-Type': 'application/json'
            }
        }
    );

    check(response, {
        'created': (r) => r.status === 201,
    });

    return response.json().shortCode;
}

export default function (shortCode) {

    const response = http.get(
        `http://localhost:8080/${shortCode}`,
        {
            redirects: 0
        }
    );

    check(response, {
        'redirect': (r) => r.status === 302,
    });
}