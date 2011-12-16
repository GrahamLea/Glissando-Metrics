Gliss Metrics is a High-Performance Metrics Library for Java

The intent is to provide:
* Monitor components that can measure the occurrence of very-high-frequency events with accuracy but also minimal concurrency overhead.
* Metric components that can build on the basic frequency counts to present more complex information, e.g. ratios of bad events.
* Alert components that can monitor metrics for situations that should be brought to the attention of someone or some thing.
* Aspect implementations that can be used to advise the Monitors of events. (Of course, you're free to just hand-code Decorators instead.)
* Examples of how to put these components together to monitor a typical system.

Present status:
* Monitors are done and seem to work well
* Aspects are done and seem to work well
* Some basic Metrics are available (Sum and Ratio)
* An example Spring Framework application demonstrating all of the above is available
* Alerts are in progress, but needs more work

If you've got any thoughts, feedback, requests or suggestions, please get in touch!
