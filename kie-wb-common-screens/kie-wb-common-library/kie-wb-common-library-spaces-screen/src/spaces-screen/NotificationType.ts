import { JavaEnum } from "appformer-js";

export class NotificationType extends JavaEnum<NotificationType> {
  public static readonly DEFAULT: NotificationType = new NotificationType("DEFAULT");
  public static readonly ERROR: NotificationType = new NotificationType("ERROR");
  public static readonly SUCCESS: NotificationType = new NotificationType("SUCCESS");
  public static readonly INFO: NotificationType = new NotificationType("INFO");
  public static readonly WARNING: NotificationType = new NotificationType("WARNING");

  protected readonly _fqcn: string = NotificationType.__fqcn();

  public static __fqcn(): string {
    return "org.uberfire.workbench.events.NotificationEvent$NotificationType";
  }

  public static values() {
    return [
      NotificationType.DEFAULT,
      NotificationType.ERROR,
      NotificationType.SUCCESS,
      NotificationType.INFO,
      NotificationType.WARNING
    ];
  }
}
