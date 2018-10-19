import * as React from "react";

interface Props {
    onClose: () => void;
}

interface State {

}

export class Popup extends React.Component<Props, State> {

    private closeWhenClickingOverlay(e: any) {
        if (e.target == e.currentTarget) {
            this.props.onClose();
        }
    }

    render() {
        //FIXME: Place this css somewhere else. This will speed up the component creation.
        return (
            <div onClick={e => this.closeWhenClickingOverlay(e)}
                 style={{
                     position: "fixed",
                     width: "100%",
                     height: "100%",
                     top: 0,
                     left: 0,
                     right: 0,
                     bottom: 0,
                     margin: "auto",
                     backgroundColor: "rgba(0,0,0, 0.5)",
                     zIndex: 10,
                 }}>
                <div style={{
                         position: "absolute",
                         left: "25%",
                         right: "25%",
                         top: "25%",
                         bottom: "75%",
                         margin: "auto",
                     }}>
                    {this.props.children}
                </div>
            </div>
        );
    }
}