# Get Me Home
A mobile app that provides driving directions without the internet

## Motivation
1. This app uses the wireless technologies of SMS and GPS to obtain driving directions in areas of limited WiFi and Cellular Data connectivity.
2. Cellular Data, even when available, is more expensive and less reliable compared to SMS, especially in rural areas and outside the United States.
3. Short messaging Service (SMS) is a text messaging service that allows users to send and receive messages through their mobile devices and applications. The reliability of SMS services makes it a preferable service for a critical task such as finding directions. Being lost in an unknown area can pose a large danger to drivers.
4. Global Positioning System (GPS) is a global navigation satellite system that uses geolocation and time estimations via satellites to provide directions to the receiver. This wireless technology, which is not dependent on Internet connectivity, can be used to locate the coordinates of the user and to draw out a route for the user to provide directions via SMS with the help of a server.

## Functionalities
1. User sends a request to the server for directions through an SMS containing the destination address and GPS location.
2. The server will access the Google Maps Directions API and retrieve the full directions.
3. The retrieved directions will be parsed to extract the instructions of the directions and the appropriate coordinates of the legs of the trip.
4. The parsed directions will be encoded and compiled into a series of text messages with headers added by the server to be sent back to the user. The headers ensure that the user application can correctly parse the data sent by the server.
5. The user application will then parse the data into directions and organize a trip for the user.
6. Directions will be spoken with audio to the users once they come within a certain distance to a turn.
7. Users will have option to request for directions again if they get lost again or if they miss a turn.

## Robustness of Communication
As the product is dependent on SMS communication, which may be unreliable, the user application has a time-out of two minutes for each request. The two minute threshold was arbitrarily chosen as an appropriate waiting period, and in testing proved to be more than enough for a response; on average responses to a request take no more than 10 seconds.

Furthermore, the server is equipped with the ability to diagnose its own network connectivity, and if it deems itself unable of processing a request by a user, a *NO SIGNAL* response will be returned to the user. Likewise, if the user makes an invalid request for directions, such as providing a non-existent destination, the server will respond with an *INVALID* response to the user application, so that the user may be notified of the bad request.

## Screenshot
![alt text][screenshot]

[screenshot]: https://github.com/dhosseinian/Get-Me-Home/blob/master/screenshot.jpg
