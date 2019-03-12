export interface Space {
  name: string;
  contributors: any[];
  repositories: any[];
}

export function fetchIsValidGroupIdName(groupId: string): Promise<boolean> {
  return fetch(`rest/spacesScreen/spaces/validGroupId?groupId=${groupId}`, {
    credentials: "same-origin"
  }).then(r => r.json());
}

export function fetchIsDuplicatedSpaceName(name: string): Promise<boolean> {
  return fetch(`rest/spacesScreen/spaces/${name}`, {
    credentials: "same-origin"
  }).then(response => response.status === 200);
}

export function fetchSpaces(): Promise<Space[]> {
  return fetch("rest/spacesScreen/spaces", { credentials: "same-origin" }).then(
    response => response.json()
  );
}

export function createSpace(newSpace: {
  name: string;
  groupId: string;
}): Promise<Response> {
  return fetch("rest/spacesScreen/spaces", {
    credentials: "same-origin",
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(newSpace)
  });
}

export function updateLibraryPreference(preference: {
  projectExplorerExpanded: boolean;
  lastOpenedOrganizationalUnit: string;
}): Promise<Response> {
  return fetch("rest/spacesScreen/libraryPreference", {
    credentials: "same-origin",
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(preference)
  });
}
