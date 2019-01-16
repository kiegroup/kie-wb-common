export class AuthorizationManager {

    public static hasAccessToPerspective(perspectiveId:string): boolean {
        return (window as any).AppFormer.HomeModelAuthorizationManager.authorize(perspectiveId);
    }

}
