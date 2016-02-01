# Payter

Payter is a simple web interface to the [Payture API](http://payture.com/integration/api/). It allows you to get a list of cards, add new cards and delete existing ones.

## Prerequisites

You will need [Leiningen][1] 2.0 or above installed.

[1]: https://github.com/technomancy/leiningen

You also need a valid Payture account. Put the `:payture-host` and `:merchant-id` to the env like that:

``` clojure
{:profiles/dev  {:env {:payture-host "sandbox.payture.com"
                       :merchant-id "7even"
                       :mock-responses? true}}
 :profiles/test {:env {:payture-host "sandbox.payture.com"}}}
```

When `:mock-responses?` is true, no real requests are being sent to Payture - the application uses mocked response XML from files.

## Running

To start a web server for the application, run:

    lein run

## License

Copyright © 2016 Vsevolod Romashov
