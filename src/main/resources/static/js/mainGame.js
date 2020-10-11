let body = $("#body");
let deck = $(".preLastCardBlock");
let addCardsBut = $("#addCards");
let takeButton = $("#takeAll");
let updateButton = $("#updateData");
let actionButton = $("#actionButton");
let myHandCards = $(".myCard");
//let enemyCards = $(".tableCard");
let enemyHandCards = $(".enemyHandCard");
//let upperCards = $(".upperCard");
let field = $("#innerField");
let myHand = $("#myHand");
let enemyHand = $('#enemyHand');
let bottomCards = $(".bottomCard");
let selectedCard = null;
let myTurn = true;
let myMove = true;
let mainTimer;
const starPolygon = "<svg  overflow=\"visible\" width=\"40px\" height=\"40px\" >\n" +
    "                        <polygon points=\"2 13 12 13 17 3 22 13 32 13 25 23 27 33 17 27 7 33 9 23\"\n" +
    "                                 stroke=\"orange\" fill=\"gold\"  stroke-width=\"2\" style=\"margin-left: 50px\"></polygon>\n" +
    "                    </svg>"
window.onload = function () {
    bindElements();
    updateStatus();

}


function bindElements(){
    $(bottomCards).each(function () {bindBottomCard(this)});
    $(myHandCards).each(function () {bindMyCard(this)});
    $(actionButton).click(function () {
        $.ajax({
            url:'/game/action',
            success: ()=> {
                updateStatus();
                resetPositionOfMyCards();
            }
        });
    });
    startTimer();


}
function startTimer() {
    mainTimer = setInterval(updateStatus,2000);
}

function updateStatus(){
    $.ajax({
        url:'/game/update',
        success : function (data) {
            myTurn = data.isMyTurn;
            myMove = data.isMyMove;
            $('#enemyName').text(data.enemyName);
            $(actionButton).text(data.actionButtonText);

            updateEnemyHandCards(data.enemyHandCards,data.enemyTakesPreLastCard,data.enemyTakesLastCard);
            updateMyHandCards(data.myHandCards,data.takesPreLastCard,data.takesLastCard);
            updateFieldCells(data.fieldCells);
            updateFinishStatus(data.isIWon,data.isILose);


        }
    });
    function updateEnemyHandCards(cardsData,takesPreLastCard,takesLastCard) {
        let enemyTakesCards = false;

        $('.tableCard').each(function () {
            let tableCard = this;
            if (!enemyTakesCards){
                $(cardsData).each(function () {
                    if(this.id === parseInt(tableCard.id)){
                        enemyTakesCards = true;
                        return false;
                    }
                });
            }
            else {
                return false;
            }

        });
        if (enemyTakesCards){
            takeCardsToEnemy();
        }
        else {
            let count = cardsData.length;
            let length = $('.enemyHandCard').length;
            if(count>length) {
                let difference = count-length;
                addCardsToEnemy(cardsData,difference,takesPreLastCard,takesLastCard);
            }
        }
    }
    function updateMyHandCards(cards,takesPreLastCard,takesLastCard) {
        let myCards = $('.myCard');

        if (myCards.length < cards.length){
            let cardsToAdd = cards.filter(card =>{
                let cardExists = false;

                $(myCards).each(function () {
                    if (parseInt($(this).attr('id')) === card.id){
                        cardExists = true;
                    }
                });
                if (!cardExists){
                    return true;
                }
            });

            let cardsToTakeFromTable = cardsToAdd.filter(card =>{
                let cardExists = false;

                $('.tableCard').each(function() {
                    if (parseInt($(this).attr('id')) === card.id){
                        cardExists = true;
                    }
                });
                if (cardExists){
                    return true;
                }
            });

            if (cardsToTakeFromTable.length > 0){
                takeCardsFromField();
            }else if (cardsToTakeFromTable.length === 0){
                takeNewCardsFromDeckToMyHand(cardsToAdd,takesPreLastCard,takesLastCard);
            }

        }
        function takeNewCardsFromDeckToMyHand(cards,takesPreLastCard,takesLastCard){
            let preLastCard = $('#preLastCard');
            let lastCard = $('#lastCard');
            let cloneCards = [];
            let newCards = [];
            let isTrumps = [];

            let limit = cards.length;
            let count = limit;

            if (takesLastCard){
                limit -= 1;
                newCards.push(lastCard);
            }

            if (takesPreLastCard && takesLastCard && count > 1 || takesPreLastCard && !takesLastCard){
                limit -= 1;
                newCards.push(preLastCard);
            }
            else {
                takesPreLastCard = false;
            }


            if (limit<0){
                limit = 0;
            }


            for (let i=0;i<limit;i++){
                let newCard = addCloneToParent(preLastCard,deck);
                $(newCard).css({position:'fixed',opacity:1});
                addCloneCard(newCard,cards[i].path,cards[i].isTrump);

            }


            if (takesPreLastCard){
                let cloneCard;
                if (!takesLastCard){
                    cloneCard = addCloneCard(preLastCard,cards[cards.length-1].path);
                }
                else {
                    cloneCard = addCloneCard(preLastCard,cards[cards.length-2].path);
                }

                $(cloneCard).removeClass('preLastCard');
            }

            if (takesLastCard){
                let cloneCard = addCloneCard(lastCard,cards[cards.length-1].path);
                $(cloneCard).removeClass('lastCard rotatedCard');
            }


            function addCloneCard(card,path,isTrump) {
                console.log("new Card "+path+" isTrump "+isTrump);
                let cloneCard = addCloneToParent(card,myHand);
                $(cloneCard).addClass('myCard');
                $(cloneCard).removeClass('blankCard');

                $(cloneCard).css({
                    position: 'relative',
                    backgroundImage:path});
                if (isTrump){
                    addStarToCard(cloneCard);

                    //addStarToCard(card);
                    //addStarWithDelay(card,500);
                }
                isTrumps.push(isTrump);
                bindMyCard(cloneCard);
                myHandCards.push(cloneCard);

                cloneCards.push(cloneCard);
                newCards.push(card);
                return cloneCard;
            }
            newCards = newCards.reverse();
            //clons = prelast last
            //mewCards =  last prelast
            $(newCards).each(function (id) {
                setTimeout(()=> {
                    let cloneCard = cloneCards[id];

                    if (takesLastCard && id === newCards.length-1){
                        rotateLastCard(lastCard);
                        moveCardToCard(cloneCard,lastCard);
                    }
                    else {
                        if (isTrumps[id]){
                            addStarWithDelay(newCards[id],500);
                        }
                        else {
                            console.log("new card not Trump");
                        }

                        flipCard(newCards[id],cards[id].path);
                        moveCardToCard(cloneCard,newCards[id]);
                    }
                    $(cloneCard).attr('id',cards[id].id);

                },500*id);
            });
        }
    }
    function updateFieldCells(cells){
        let enemyCards = $('.enemyHandCard');
        let tableCards = $('.tableCard');
        let upperCards = $('.upperCard');
        let requiredCells;

        requiredCells = cells.filter(cell =>{
            let cardExists = false;

            $(tableCards).each(function () {
                if (parseInt($(this).attr('id')) === cell.bottomCard.id){
                    cardExists = true;
                }
            });
            if (!cardExists){
                return true;
            }
        });

        if (requiredCells.length > 0){
            addBottomCards(requiredCells)
        }


        requiredCells = cells.filter(cell =>{
            let cardExists = false;
            let upperCard = cell.upperCard;
            if (upperCard != null){

                $(upperCards).each(function () {
                    if (parseInt($(this).attr('id')) === upperCard.id){
                        cardExists = true;
                    }
                });
                if (!cardExists){
                    return true;
                }
            }
        });
        if (requiredCells.length > 0){
            addUpperCards(requiredCells);
        }

        if (cells.length === 0 && tableCards.length > 0){
            $(tableCards).animate({
                opacity:0
            },300,function () {
                $(tableCards).remove();
            });

        }



        function addBottomCards(cells) {
            $(cells).each(function () {
                let enemyCard = findEnemyCardById(this.bottomCard.id);
                moveEnemyCardToTable(enemyCard,this.bottomCard.isTrump,cutImagePath(this.bottomCard.path));
            });
        }

        function moveEnemyCardToTable(card,cardIsTrump,path){
            let cloneCard = putCardOnTable(card);
            $(cloneCard).css({backgroundImage:path});
            $(cloneCard).removeClass('blankCard enemyHandCard');
            $(card).css({position:'fixed',zIndex:1,opacity: 1});
            console.log("IsTrump "+cardIsTrump);
            if (cardIsTrump){
                addStarToCard(cloneCard);
                addStarWithDelay(card,500);
            }
            flipCard(card,path);

            bindBottomCard(cloneCard);
        }

        function addUpperCards(cells) {
            $(cells).each(function () {
                let upperCard = this.upperCard;
                let bottomCard = findBottomCard(this.bottomCard.id);
                let enemyCard = findEnemyCardById(upperCard.id);
                putEnemyCardToCard(enemyCard,upperCard.isTrump,bottomCard,cutImagePath(upperCard.path));
            });
        }
        function findBottomCard(id) {
            let fundedCard = null;
            $('.bottomCard').each(function () {
                if (parseInt(this.id) === id){
                    fundedCard = this;
                    return false;
                }
            });
            return fundedCard;
        }
        function findEnemyCardById(id) {
            let fundedCard = null;
            $(enemyCards).each(function () {
                if (parseInt($(this).attr('id')) === id){
                    fundedCard = this;
                    return false;
                }
            });
            return fundedCard;
        }
        function putEnemyCardToCard(enemyCard,cardIsTrump,bottomCard,path) {
            $(enemyCard).css({zIndex: 1});
            let cloneCard = putCardOnCard(bottomCard,enemyCard);
            $(cloneCard).css({backgroundImage:path});
            $(cloneCard).removeClass('enemyHandCard blankCard');
            if (cardIsTrump){
                addStarToCard(cloneCard);
                addStarWithDelay(enemyCard,500);
            }
            flipCard(enemyCard,path);
        }

        function cutImagePath(path) {
            return path.substring(17,path.length);
        }
    }
    function updateFinishStatus(isIWon,isILose) {
        if (isIWon){
            $("#gameFinishedModal").removeClass('displayNone');
            $("#modalH1").text("Congratulation! you are NOT DURAK");
            setTimeout(()=>{
                $("#gameFinishedModal").css({top: 50+'%'});
            },1);
        }
        else if (isILose){
            $("#gameFinishedModal").removeClass('displayNone');
            $("#modalH1").text("Sorry but you are DURAK");
            $("#modalH2").text("good luck")
            setTimeout(()=>{
                $("#gameFinishedModal").css({top: 50+'%'});
            },1);
        }
    }
}
function bindBottomCard(card){
    $(card).click(function () {
        if (selectedCard!==null){
            tryToPutCardOnCard(card,selectedCard);
        }
    });
}
function bindMyCard(card){
    $(card).click(function () {
        if (myMove){
            selectMyCard(card);
        }
    });
    function selectMyCard(card) {
        if (selectedCard !== card){
            selectedCard = card;
            resetPositionOfMyCards();
            $(card).animate({
                bottom:30
            },300);
            //$(card).removeClass('myCard');
        }
        else {
            selectedCard = null;
            $(card).animate({
                bottom:0
            },300);
        }


    }
}
function takeCardsToEnemy() {
    let delay=0;
    let tableCards = $('.tableCard');
    let bottomCards = [];
    let upperCards = [];
    enemyHandCards = $(".enemyHandCard");
    let cardsToFlip = [];
    let blankPath = "url('/images/cards/back.png')";

    $(tableCards).each(function () {
        $(this).removeClass('tableCard');
        if ($(this).hasClass('upperCard')){
            upperCards.push(this);
        }
        else {
            bottomCards.push(this);
        }
    });

    if (upperCards.length>0){
        delay = 1200;
    }

    let iterator=enemyHandCards.length;
    let newCardsStart = iterator;
    for (let i=0;i<upperCards.length+bottomCards.length;i++){
        //let cloneCard = addCloneToParent(enemyHand);
        let cloneCard = '<div style="opacity: 0" class="gameCard blankCard enemyHandCard" ></div>';
        $(enemyHand).append(cloneCard);
    }

    enemyHandCards = $(".enemyHandCard");

    $(upperCards).each(function () {
        addCloneToHand(this);
    });

    $(bottomCards).each(function () {
        addCloneToHand(this);
    });

    iterator = newCardsStart;

    $(upperCards).each(function () {
        $(this).removeClass('upperCard');
        $(this).css({zIndex: 1});
        moveCardToCard(enemyHandCards[iterator],this);
        iterator++;
    });


    setTimeout(()=> {
        iterator = enemyHandCards.length-1;
        for (let i=bottomCards.length-1;i>=0;i--){
            moveCardToCard(enemyHandCards[iterator],bottomCards[i]);
            iterator--;
        }
    },delay);

    setTimeout(()=> {
        $(cardsToFlip).each(function () {
            removeStarWithDelay(this,500);
            flipCard(this,blankPath);
        });
    },1500+delay);


    function addCloneToHand(card) {
        let cloneCard = $(enemyHandCards[iterator]);
        $(cloneCard).attr('id',$(card).attr('id'));
        let path = subStringPath($(card).css('background-image'));
        $(cloneCard).css({backgroundImage:path});
        if ($(card).has("svg").length){
            console.log("has svg "+$(card).css('background-image'));
            $(cloneCard).append($(starPolygon));
        }
        cardsToFlip.push(cloneCard);
        iterator++;
    }


}
function addCardsToEnemy(cardsData,count,takesPreLastCard,takesLastCard) {
    console.log("last "+takesLastCard+" prelast "+takesPreLastCard)
    let iterator = 0;

    let preLastCard = $('#preLastCard');
    let lastCard = $('#lastCard');
    let lastClone;
    let newCards = [];
    let cloneCards = [];
    let limit = count;

    //takesPreLastCard = takesPreLastCard && preLastCard !== undefined;
    if (takesLastCard){
        limit -= 1;
        newCards.push(lastCard);
    }
    if (takesPreLastCard && takesLastCard && count > 1 || takesPreLastCard && !takesLastCard){
        limit -= 1;
        newCards.push(preLastCard);
    }
    else {
        takesPreLastCard = false;
    }

    if (limit < 0){
        limit = 0;
    }

    cardsData = cardsData.slice(cardsData.length-count);

    for (let i=0;i<limit;i++){
        let newCard = addCloneToParent(preLastCard,deck);
        let cloneCard = addCloneToParent(newCard,enemyHand);
        $(newCard).css({position:'fixed',opacity:1});
        $(cloneCard).removeClass('preLastCard');
        $(cloneCard).addClass('enemyHandCard');
        $(cloneCard).attr('id',cardsData[iterator].id);
        iterator++;
        newCards.push(newCard);
        cloneCards.push(cloneCard);
    }


    if (takesPreLastCard){
        let cloneCard = addCloneToParent(preLastCard,enemyHand);
        $(cloneCard).removeClass('preLastCard');
        $(cloneCard).addClass('enemyHandCard');
        $(cloneCard).attr('id',cardsData[iterator].id);
        iterator++;
        cloneCards.push(cloneCard);
    }
    if (takesLastCard){
        lastClone = addCloneToParent(lastCard,enemyHand);
        $(lastClone).removeClass('rotatedCard lastCard');
        $(lastClone).addClass('blankCard enemyHandCard');
        $(lastClone).attr('id',cardsData[iterator].id);
        iterator++;
        cloneCards.push(lastClone);
    }

    newCards = newCards.reverse();
    //clons = prelast last
    //mewCards = last prelast
    $(newCards).each(function (id) {
        setTimeout(function () {
            let cloneCard = cloneCards[id];

            if (takesLastCard && id === newCards.length-1){
                console.log("takes last")
                rotateLastCard(lastCard);
                moveCardToCard(cloneCard,lastCard);
                setTimeout(function () {
                    removeStarWithDelay(lastClone,500);
                    flipCard(lastClone,"url(\"/images/cards/back.png\")");
                },1000);
            }
            else {
                console.log("takes preLast")
                moveCardToCard(cloneCard,newCards[id]);
            }

        },300*id);
    });

}





$(field).click(function (event) {
    if (event.target===this){
        if (selectedCard!==null){
            tryToPutCardOnTable(selectedCard);
            selectedCard = null;
        }
    }

});
function tryToPutCardOnTable(card) {
    $.ajax({
        url:'/game/putCardOnTable',
        data:{cardId:$(card).attr('id')},
        success : function (data) {
            if (data === true){
                putCardOnTable(card);
            }
        }
    });
}
function putCardOnTable(card){
    let clone = addCloneToParent(card,field);
    $(clone).addClass('tableCard bottomCard');
    $(clone).removeClass('myCard');
    resetCardPos(clone);
    moveCardToCard(clone,card);
    return clone;
}

function takeCardsFromField(){
    let delay=0;
    let tableCards = $('.tableCard');
    let bottomCards = [];
    let upperCards = [];
    let clones = [];

    clearInterval(mainTimer);

    $(tableCards).each(function () {
        $(this).removeClass('tableCard');
        if ($(this).hasClass('upperCard')){
            upperCards.push(this);
        }
        else {
            bottomCards.push(this);
        }
    });
    if (upperCards.length>0){
        delay = 1200;
    }

    $(upperCards).each(function () {
        let card = this;
        $(card).removeClass('upperCard');
        let newCard = addCardCloneToMyHand(card);
        $(card).css({zIndex: 1});

        setTimeout(()=> {
            moveCardToCard(newCard,card);
        },100);
    });

    setTimeout(()=> {
        $(bottomCards).each(function () {
            $(this).css({zIndex: 1});
            let cloneCard = addCardCloneToMyHand(this);
            cloneCard.removeClass('bottomCard');
            clones.push(cloneCard);
        });

        let cardsToMove = bottomCards.length-1;

        setTimeout(()=> {
            for (let i=cardsToMove;i>=0;i--){
                moveCardToCard(clones[i],bottomCards[i]);
            }
            setTimeout(()=> {
                startTimer();
            },cardsToMove * 100);
        },100);


    },delay);
    function addCardCloneToMyHand(card) {
        $(card).addClass('myCard');
        let cloneCard = addCloneToParent(card,myHand)
        bindMyCard(cloneCard);
        myHandCards.push(cloneCard);
        return cloneCard;
    }
}


function addCloneToParent(card,parentNode) {
    let clone = $(card).clone();
    $(clone).css('opacity','0');
    $(parentNode).append(clone);
    return clone;
}
function subStringPath(path) {
    return "url(\""+ path.substring(path.indexOf("/images"));
}

function flipCard(card,newPath) {
    rotateCard(card,0,90,500);
    setImageWithDelay(card,newPath,500);
    setTimeout(function () {

        rotateCard(card,270,360,500);
    },500);
    setInterval(function () {
        $(card).css('transform', 'rotateY(' + 0 + 'deg)');
    },1000);

    function rotateCard(card,fromDeg,toDeg,duration) {
        $({rotation: fromDeg}).animate({rotation: toDeg},
            { duration: duration,easing: 'swing',step: function () {
                    $(card).css('transform', 'rotateY(' + this.rotation + 'deg)');
                }});
    }
    function setImageWithDelay(card,path,delay) {
        setTimeout(function () {
            $(card).css({backgroundImage:path});
        },delay);
    }
}

function rotateLastCard(lastCard) {
    $({deg:-90}).animate({deg:0},{
        duration:1000,
        step: function (now) {
            $(lastCard).css({transform: 'rotate('+now+'deg)'});
        }
    });
}
function tryToPutCardOnCard(cardTo,card) {
    $.ajax({
        url:'/game/putCardOnCard',
        data:{cardToId:$(cardTo).attr('id'),cardId:$(card).attr('id')},
        success:function (data) {
            if (data===true){
                putCardOnCard(cardTo,card);
            }
        }
    });
}
function putCardOnCard(cardTo,card) {
    $(card).unbind();
    $(card).removeClass('myCard');
    let clone = addCloneToParent(card,cardTo);
    $(clone).addClass('upperCard tableCard');
    $(cardTo).removeClass('bottomCard');

    fitCardSize(cardTo,clone);
    resetCardPos(selectedCard);
    resetCardPos(clone);
    moveCardToCard(clone,card);
    return clone;

    function fitCardSize(cardTo,card) {
        let cardWidth = $(card).width();
        let cardToWidth = $(cardTo).width();
        if (cardWidth!==cardToWidth){
            $(card).css('width',cardToWidth);
            $(card).css('height',$(cardTo).height());
        }
    }
}

function moveCardToCard(cardTo,card) {
    //$(cardTo).css({position:'relative'});

    let cord = $(card).offset();
    $(card).css({top:cord.top,left:cord.left,position:'fixed'});
    let offset = $(cardTo).offset();
    let x = offset.left;
    let y = offset.top;


    let cardWidth = $(card).width();
    let cardToWidth = $(cardTo).width();

    if (cardWidth!==cardToWidth){
        $(card).animate({
            left:x,
            top:y,
            height:$(cardTo).height(),
            width :cardToWidth
        },1000,function () {
            showCloneCard();
        });
    }
    else{
        $(card).animate({
            left:x,
            top:y
        },1000,function () {
            showCloneCard();
        });
    }
    function showCloneCard() {
        $(cardTo).css('opacity','1');

        if (selectedCard===card){
            selectedCard = null;
        }
        $(card).remove();
    }
}


function resetCardPos(card) {
    $(card).css('bottom','0');
}
function resetPositionOfMyCards() {
    $(myHandCards).each(function () {
        $(this).animate({
            bottom:0
        },100);
    });
}
function addStarWithDelay(card,delay) {
    setTimeout(function () {
        addStarToCard(card);
    },delay);
}
function addStarToCard(card) {
    $(card).append($(starPolygon));
}
function removeStarWithDelay(card,delay) {
    setTimeout(()=>{
        $(card).empty();
    },delay)
}


