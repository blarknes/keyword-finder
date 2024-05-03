# keyword-finder

The focus of keyword-finder is, as the name say, look for specific **keywords** on a provided **website** an its associated **subdomains**.

## running in docker

To run the application in Docker, you simply build the image.

```sh
docker build . -t keywordfinder
```

Then run the container in the default spring boot port.

```sh
docker run -p 8080:8080 --rm keywordfinder
```

## how it works

At present, the application consists of two distinct Controllers: **Healthcheck** and **Search**.

The sole objective of the Healthcheck Controller is to provide a status on the Application's operational state. It achieves this by simply returning a straightforward `200 OK` response to any incoming requests, confirming that the Application is running.

Within the **Search** Controller is where the _Magic_ happens. It's where the two main functionalities are implemented: schedule and display.

The **schedule** assumes the responsibility of asynchronously search for the desired keyword and any new subdomains present in anchor tags found within the HTML of the provided website. For each new encountered subdomain, the process is repeated, recording every domain passed through and if the keyword was found or not.

The **display** takes charge of presenting to the user a report of all searches made while the application was running, as well as, detailed information of any search made.

## using the application

The usage of the application is quite straightforward. You simply need to run it and utilize the `/search` endpoint, as demonstrated in the [endpoints](#endpoints) section.

## endpoint list

<details>
<summary>search keyword</summary>

### request

`baseurl` must be a valid url.  
`keyword` length must be a anywhere between 4 through 32.

```http
POST /search HTTP/1.1
Host: localhost:8080
Content-Type: application/json
Body:
{
    "baseurl": "https://magic.wizards.com/en/news",
    "keyword": "arena"
}
```

### response

```http
201 CREATED
Content-Type: application/json
Body:
{
    "id": "UsgTfB70"
}
```

### response (invalid body)

```http
400 Bad Request
Content-Type: application/json
Body:
{
    "timestamp": "2023-01-01T04:44:44.302067400Z",
    "status": 400,
    "error": "Bad Request",
    "message": [
        "The baseurl must be a valid url",
        "The keyword size must be between 4 and 32"
    ],
    "path": "/search"
}
```

</details>

<details>
<summary>search status</summary>

### request

`id` must refer to an existent id.

```http
GET /search/{{id}} HTTP/1.1
Host: localhost:8080
Content-Type: application/json
```

### response

```http
200 OK
Content-Type: application/json
Body:
{
    "id": "UsgTfB70",
    "keyword": "magic",
    "baseurl": "https://magic.wizards.com/en/news,
    "status": "running",
    "looked": 1,
    "found": 2,
    "urls": [
        "https://magic.wizards.com/en/news",
        "https://magic.wizards.com/en/news/archive?author=4bUf4MDTiLi6jOKxDj3KQm"
    ]
}
```

### response (invalid id)

```http
400 Bad Request
Content-Type: application/json
Body:
{
    "timestamp": "2023-11-08T04:49:10.729+00:00",
    "status": 400,
    "error": "Bad Request",
    "message": "The ID invalid_id does not exist",
    "path": "/search/invalid_id"
}
```

</details>

<details>
<summary>status list</summary>

### request

```http
GET /search HTTP/1.1
Host: localhost:8080
Content-Type: application/json
```

### response

```http
200 OK
Content-Type: application/json
Body:
{
    "searches": [
        {
            "id": "djoLV6av",
            "keyword": "magic",
            "baseurl": "https://magic.wizards.com/en/news,
            "status": "done",
            "looked": 1,
            "found": 44
        },
        {
            "id": "594epjVV",
            "keyword": "magic",
            "baseurl": "https://magic.wizards.com/en/news,
            "status": "runnning",
            "looked": 1,
            "found": 4
        }
    ]
}
```

</details>

<details>
<summary>healthcheck</summary>

### request

```http
GET /healthcheck HTTP/1.1
Host: localhost:8080
```

### response

```http
200 OK
Body:
OK
```

</details>
