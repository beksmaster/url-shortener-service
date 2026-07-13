import http from 'k6/http';

export const options = {
    vus: 500,
    duration: '30s',
};

export default function () {

    const payload = JSON.stringify({
        originalUrl: 'https://roadmap.sh/java',
        expiresAt: '2027-01-01T00:00:00Z'
    });

    http.post(
        'http://localhost:8080',
        payload,
        {
            headers: {
                'Content-Type': 'application/json',
            },
        }
    );
}