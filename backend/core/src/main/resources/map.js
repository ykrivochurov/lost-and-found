function () {
  if (!this.closed) {
    emit(this.tags[0], 1);
  }
}