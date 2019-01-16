import { HomeScreen } from "./HomeScreen";
import { Profile } from "@kiegroup-ts-generated/kie-wb-common-profile-api";

export interface HomeScreenProvider {
  get(profile: Profile): HomeScreen;
}
