$(document).ready(function (){
  setup();

  $('.add-todo-list').on("click", function(e){
    e.preventDefault();
    $.ajax({
        type: 'POST',
        url: 'add-todo-list',
        data: {},
        dataType: 'html',
        success: function (data) {
          var p = $('.center');
          p.html(p.html() + data);
          $(this).remove();
          p.add($(this));
          setup();
        },
        error: function (data) {
          console.log("ERROR " + data.responseText);
        }
    });
  });
});

function setup() {
  $('.delete-btn').on("click", function (e) {
    var p = $(e.target).parent();
    var row = $($(p).parent()).parent();
    $.ajax({
        type: 'POST',
        url: 'delete',
        data: {
          proj: row.parent().find('#proj').val(),
          task: row.find('.task').text()
        },
        dataType: 'html',
        success: function (data) {
          console.log("SUCCESS");
          $('#result').html(data);
          row.remove();
        },
        error: function (data) {
          console.log("ERROR " + data.responseText);
          $('#result').html(data.msg);
        }
    });
    e.preventDefault();
  });


  $('.addtaskform').on("submit", function(e){
    e.preventDefault();
    $.ajax({
        type: 'POST',
        url: 'list',
        data: $(this).serialize(),
        dataType: 'html',
        success: function (data) {
          console.log(e);
          console.log("SUCCESS: " + data);
          var parent = $($(e.target).parent());
          var p = parent.find('.project');
          p.html(p.html() + data);
          parent.find('input[type=text]').val('');
          setup();
        },
        error: function (data) {
          console.log("ERROR " + data.responseText);
        }
    });
  });


  $('.delete-btn-proj').on("click", function (e) {
    var p = $(e.target).parent();
    var row = $(p).parent();
    $.ajax({
        type: 'POST',
        url: 'delete-project',
        data: {
          proj: row.find('.proj-title').html(),
        },
        dataType: 'html',
        success: function (data) {
          row.next("div").remove();
          row.remove();
        },
        error: function (data) {
          console.log("ERROR " + data.responseText);
          $('#result').html(data.msg);
        }
    });
    e.preventDefault();
  });


  };

