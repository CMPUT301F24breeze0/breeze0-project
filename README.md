| Database       | Field                      | Type                                        | Description                                                                 |
|----------------|----------------------------|---------------------------------------------|-----------------------------------------------------------------------------|
| **EntrantDB**  | `entrant_id` (document_id) | `string`                                    | Unique ID for the entrant                                                   |
|                | `name`                     | `string`                                    | Entrant's name                                                              |
|                | `email`                    | `string`                                    | Entrant's email                                                             |
|                | `phone number`             | `string`                                    | Entrant's phone number                                                      |
|                | `profile_photo`            | `string`                                    | Path or URL to the entrant's profile photo                                  |
|                | `device`                   | `string`                                    | Device information                                                          |
|                | `events_id`                | `array<event_id:string, location:string>`   | List of events the entrant is participating in, with event ID and location  |
|                | `status`                   | `array<event_id:string, status:string>`     | List of events with status for each, where status could be "waiting", "enrolled", etc. |
| **OrganizerDB**| `organizer_id` (document_id)| `string`                                    | Unique ID for the organizer                                                 |
|                | `name`                     | `string`                                    | Organizer's name                                                            |
|                | `email`                    | `string`                                    | Organizer's email                                                           |
|                | `phone number`             | `string`                                    | Organizer's phone number                                                    |
|                | `device`                   | `string`                                    | Device information                                                          |
|                | `events_id`                | `array<event_id:string>`                    | List of event IDs organized by the organizer                                |
| **OverallDB**  | `event_id` (document_id)   | `string`                                    | Unique ID for the event                                                     |
|                | `name`                     | `string`                                    | Name of the event                                                           |
|                | `QRcode`                   | `string`                                    | QR code for the event                                                       |
|                | `poster_photo`             | `string`                                    | Path or URL to the event's poster photo                                     |
|                | `facility`                 | `string`                                    | Facility where the event is held                                            |
|                | `start_date`               | `string`                                    | Event start date                                                            |
|                | `end_date`                 | `string`                                    | Event end date                                                              |
|                | `entrants`                 | `array<entrant_id:string>`                  | List of entrant IDs participating in the event                              |
|                | `organizers`               | `array<organizer_id:string>`                | List of organizer IDs managing the event                                    |
|                | `limited_number`           | `integer`                                   | Maximum number of entrants allowed for the event                            |
| **FacilityDB** | `facility_id` (document_id)| `string`                                    | Unique ID for the facility                                                  |
|                | `location`                 | `string`                                    | Location of the facility                                                    |
|                | `device`                   | `string`                                    | Device information                                                          |
| **AdminDB**    | `admin_id` (document_id)   | `string`                                    | Unique ID for the admin                                                     |
|                | `device`                   | `string`                                    | Device information                                                          |
