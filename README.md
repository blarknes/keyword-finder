# keyword-finder

This application serves the fundamental objective of meticulously identifying if precise **keywords** (ignoring case sensivity) are located within a designated **website** and its associated **subdomains**. Moreover, it offers users a comprehensive array of sophisticated tools to effortlessly examine the ongoing succinct overview of all conducted searches, or alternatively, delve into an intricate analysis of the granular status pertaining to a particular search.

# how it works

By default, the application runs on port 4576, which is the port provided by Spark. However, you have the flexibility to modify this setting in the [Router.java](src\main\java\com\keywordfinder\Router.java) file.

The **Application** file acts as the orchestrator, invoking the **Router** to configure all possible routes within the API. Each Controller is instantiated within this file, allowing for consistent handling of **all** incoming requests through the same class instances.

At present, the application consists of two distinct Controllers: the **Healthcheck** and the **Crawl**.

The primary objective of the Healthcheck Controller is to provide a status update on the Application's operational state. It achieves this by simply returning a straightforward `200 OK` response to incoming requests, confirming that the Application is running as expected.

Within the **Crawl** Controller lies the heart of the application's functionality. Here, two main functionalities are implemented: listing and scheduling.

The **Lister** takes charge of presenting the user with a comprehensive overview of all ongoing searches, providing the information in a concise and easily digestible format. Additionally, it offers the capability to retrieve detailed information on a specific search, presenting a more elaborate view of the results.

The **Scheduler**, on the other hand, assumes the responsibility of executing the search function asynchronously. Its primary purpose is to search for the **desired keyword** and any **new subdomains** present in anchor tags within the HTML of the **provided website specified in the user's request**. For each new subdomain encountered, the process is repeated, diligently recording all domains that contain the desired keyword in the information result object. This enables real-time monitoring of the ongoing searches and allows users to identify the domains where the desired keyword has been found.

# using the application

The usage of the application is quite straightforward. You simply need to run it and utilize the `/crawl` endpoint, as demonstrated in the [endpoints](#endpoints) section.

# field validations

**baseurl** must be a valid url.  
**keyword** length must be a anywhere between four (4) through thirty-two (32).  
**id** must refer to an existent id.

# endpoint list

<details>
<summary>search keyword</summary>

### request

```http
POST /crawl HTTP/1.1
Host: localhost:4567
Content-Type: application/json
Body:
{
    "baseurl": "https://magic.wizards.com/en/news",
    "keyword": "arena"
}
```

### response

```http
200 OK
Content-Type: application/json
Body:
{
    "id": "UsgTfB70"
}
```

### response (invalid baseurl)

```http
400 Bad Request
Content-Type: application/json
Body:
{
    "reason": "Invalid value for field `baseurl`. Not a valid URL."
}
```

### response (invalid keyword)

```http
400 Bad Request
Content-Type: application/json
Body:
{
    "reason": "Invalid size for field `keyword`. Must be a anywhere between four (4) through thirty-two (32)."
}
```

</details>

<details>
<summary>search status</summary>

### request

```http
POST /crawl/{{id}} HTTP/1.1
Host: localhost:4567
Content-Type: application/json
```

### response

```http
200 OK
Content-Type: application/json
Body:
{
    "id": "OBkOrwre",
    "status": "active",
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
    "reason": "Invalid value for field `id`. This id does not exist."
}
```

</details>

<details>
<summary>status list</summary>

### request

```http
POST /crawl/list HTTP/1.1
Host: localhost:4567
Content-Type: application/json
```

### response

```http
200 OK
Content-Type: application/json
Body:
{
    "active": [
        "OBkOrwre: keyword found in 0 urls"
    ],
    "done": [
        "f1fm4K8b: keyword found in 44 urls"
    ]
}
```

</details>

<details>
<summary>healthcheck</summary>

### request

```http
GET /healthcheck HTTP/1.1
Host: localhost:4567
```

### response

```http
200 OK
Body:
OK
```

</details>
