import * as React from "react";
import * as AppFormer from "appformer-js";
import { Popup } from "./Popup";

export function LoadingPopup() {
  return (
    <Popup
      onClose={() => {
        /* do nothing */
      }}
    >
      <div
        className="well"
        style={{
          width: "300px",
          float: "none",
          margin: "auto",
          textAlign: "center"
        }}
      >
        <div className="spinner spinner-lg" />
        <span>{AppFormer.translate("Loading", [])}</span>
      </div>
    </Popup>
  );
}
