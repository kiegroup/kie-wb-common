import { Portable } from "appformer-js";
import { JavaInteger } from "appformer-js";
import { NotificationType } from "./NotificationType";

export class NotificationEvent implements Portable<NotificationEvent> {
  protected readonly _fqcn: string = NotificationEvent.__fqcn();

  public readonly notification?: string = undefined;
  public readonly type?: NotificationType = undefined;
  public readonly isSingleton?: boolean = undefined;
  public readonly initialTopOffset?: JavaInteger = undefined;

  constructor(self: {
    notification?: string;
    type?: NotificationType;
    isSingleton?: boolean;
    initialTopOffset?: JavaInteger;
  }) {
    Object.assign(this, self);
  }

  public static __fqcn(): string {
    return "org.uberfire.workbench.events.NotificationEvent";
  }
}
